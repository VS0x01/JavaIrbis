// This is an open source non-commercial project. Dear PVS-Studio, please check it.
// PVS-Studio Static Code Analyzer for C, C++ and C#: http://www.viva64.com

package ru.arsmagna.infrastructure;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ru.arsmagna.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Импорт и экспорт записей в формате ISO2709.
 */
@SuppressWarnings("WeakerAccess")
public class Iso2709 {

    /**
     * Length of the record marker.
     */
    public static final int MARKER_LENGTH = 24;

    /**
     * Record delimiter.
     */
    public static final byte RECORD_DELIMITER = 0x1D;

    /**
     * Field delimiter.
     */
    public static final byte FIELD_DELIMITER = 0x1E;

    /**
     * Subfield delimiter.
     */
    public static final byte SUBFIELD_DELIMITER = 0x1F;

    //=========================================================================

    /**
     * Parse the stream for the record.
     *
     * @param stream  Stream to parse.
     * @param charset Charset to use.
     * @return Parsed record or null if end-of-stream detected.
     */
    @Nullable
    public static MarcRecord readRecord(@NotNull InputStream stream,
                                        @NotNull Charset charset)
            throws IOException {
        MarcRecord result = new MarcRecord();

        // Считываем длину записи
        byte[] marker = new byte[5];
        if (stream.read(marker) != marker.length) {
            return null;
        }

        // а затем и ее остаток
        int recordLength = FastNumber.parseInt32(marker, 0, marker.length);
        byte[] record = new byte[recordLength];
        int need = recordLength - marker.length;
        if (stream.read(record, marker.length, need) != need) {
            return null;
        }

        // Простая проверка, что мы имеем дело
        // с нормальной ISO-записью
        if (record[recordLength - 1] != RECORD_DELIMITER) {
            return null;
        }

        int indicatorLength = FastNumber.parseInt32(record, 10, 1);
        int baseAddress = FastNumber.parseInt32(record, 12, 5);

        // Пошли по полям при помощи справочника
        for (int directory = MARKER_LENGTH; ; directory += 12) {
            // Переходим к следующему полю.
            // Если нарвались на разделитель, значит, справочник закончился
            if (record[directory] == FIELD_DELIMITER) {
                break;
            }

            int tag = FastNumber.parseInt32(record, directory, 3);
            int fieldLength = FastNumber.parseInt32(record, directory + 3, 4);
            int fieldOffset = baseAddress
                    + FastNumber.parseInt32(record, directory + 7, 5);
            RecordField field = new RecordField(tag);
            result.fields.add(field);
            if (tag < 10) {
                // Фиксированное поле
                // не может содержать подполей и индикаторов
                field.value = charset.decode(ByteBuffer.wrap(record, fieldOffset, fieldLength - 1)).toString();
            } else {
                // Поле переменной длины
                // Содержит два однобайтных индикатора
                // может содерджать подполя

                // пропускаем индикаторы
                int start = fieldOffset + indicatorLength;
                int stop = fieldOffset + fieldLength - indicatorLength + 1;
                int position = start;

                // Ищем значение поля до первого разделителя
                while (position < stop) {
                    if (record[start] == SUBFIELD_DELIMITER) {
                        break;
                    }
                    position++;
                }

                // Если есть текст до первого разделителя, запоминаем его
                if (position != start) {
                    field.value = charset.decode(ByteBuffer.wrap(record, start, position - start)).toString();
                }

                // Просматриваем подполя
                start = position;
                while (start < stop) {
                    position = start + 1;
                    while (position < stop) {
                        if (record[position] == SUBFIELD_DELIMITER) {
                            break;
                        }
                        position++;
                    }
                    SubField subField = new SubField((char) record[start + 1],
                            charset.decode(ByteBuffer.wrap(record, start + 2, position - start - 2)).toString());
                    field.subFields.add(subField);
                    start = position;
                }
            }
        }

        return result;
    }

    private static void _Encode(byte[] bytes, int pos, int len, int val) {
        len--;
        for (pos += len; len >= 0; len--) {
            bytes[pos] = (byte) (val % 10 + (byte) '0');
            val /= 10;
            pos--;
        }
    }

    @Contract("_, _, null, _ -> param2")
    private static int _Encode(byte[] bytes, int pos, String str, Charset encoding) {
        if (str != null) {
            byte[] encoded = str.getBytes(encoding);
            for (int i = 0; i < encoded.length; pos++, i++) {
                bytes[pos] = encoded[i];
            }
        }

        return pos;
    }

    /**
     * Write the record to the stream.
     *
     * @param stream   Stream to use
     * @param record   Record to write
     * @param encoding Encoding
     * @throws IOException Output error
     */
    public static void writeRecord(@NotNull OutputStream stream,
                                   @NotNull MarcRecord record,
                                   @NotNull Charset encoding)
            throws IOException, IrbisException {
        int recordLength = MARKER_LENGTH;
        int dictionaryLength = 1; // С учетом ограничителя справочника
        int[] fieldLength = new int[record.fields.size()]; // Длины полей

        // Сначала подсчитываем общую длину
        Iterator<RecordField> fields = record.fields.iterator();
        for (int i = 0; i < record.fields.size(); i++) {
            dictionaryLength += 12; // Одна статья справочника
            RecordField field = fields.next();

            if (field.tag <= 0 || field.tag >= 1000) {
                throw new IrbisException("wrong field: " + field.tag);
            }

            int fldlen = 0;
            if (field.tag < 10) {
                // В фиксированном поле не бывает подполей и индикаторов
                fldlen += IrbisEncoding.getByteCount(field.value, encoding);
            } else {
                fldlen += 2; // Индикаторы
                if (field.value != null) {
                    fldlen += IrbisEncoding.getByteCount(field.value, encoding);
                }
                Iterator<SubField> subfields = field.subFields.iterator();
                for (int j = 0; j < field.subFields.size(); j++) {
                    SubField subfield = subfields.next();

                    if (subfield.code <= ' ' || subfield.code >= 127) {
                        throw new IrbisException("wrong code: " + subfield.code);
                    }

                    fldlen += 2; // Признак подполя и его код
                    if (subfield.value != null) {
                        fldlen += IrbisEncoding.getByteCount(subfield.value, encoding);
                    }
                }
            }

            fldlen++; // Разделитель полей

            if (fldlen >= 10_000) {
                throw new IrbisException("field too long");
            }

            fieldLength[i] = fldlen;
            recordLength += fldlen;
        }

        recordLength += dictionaryLength; // Справочник
        recordLength++; // Разделитель записей

        if (recordLength >= 100_000) {
            throw new IrbisException("record too long");
        }

        // Приступаем к кодированию
        int dictionaryPosition = MARKER_LENGTH;
        int baseAddress = MARKER_LENGTH + dictionaryLength;
        int currentAddress = baseAddress;
        byte[] bytes = new byte[recordLength]; // Закодированная запись

        // Маркер записи
        Arrays.fill(bytes, (byte) ' ');
        _Encode(bytes, 0, 5, recordLength);
        _Encode(bytes, 12, 5, baseAddress);
        bytes[5] = 'n';  // Record status
        bytes[6] = 'a';  // Record type
        bytes[7] = 'm';  // Bibligraphical index
        bytes[8] = '2';
        bytes[10] = '2';
        bytes[11] = '2';
        bytes[17] = ' '; // Bibliographical level
        bytes[18] = 'i'; // Cataloging rules
        bytes[19] = ' '; // Related record
        bytes[20] = '4'; // Field length
        bytes[21] = '5'; // Field offset
        bytes[22] = '0';

        // Конец справочника
        bytes[baseAddress - 1] = FIELD_DELIMITER;

        // Проходим по полям
        fields = record.fields.iterator();
        for (int i = 0; i < record.fields.size(); i++) {
            RecordField field = fields.next();

            // Справочник
            _Encode(bytes, dictionaryPosition, 3, field.tag);
            _Encode(bytes, dictionaryPosition + 3, 4, fieldLength[i]);
            _Encode(bytes, dictionaryPosition + 7, 5, currentAddress - baseAddress);
            dictionaryPosition += 12;

            // Собственно поле
            if (field.tag < 10) {
                // В фиксированных полях не бывает подполей и индикаторов
                currentAddress = _Encode(bytes, currentAddress, field.value, encoding);
            } else {
                // Индискаторы
                bytes[currentAddress++] = ' ';
                bytes[currentAddress++] = ' ';

                // Значение поля
                currentAddress = _Encode(bytes, currentAddress, field.value, encoding);

                // Подполя
                Iterator<SubField> subfields = field.subFields.iterator();
                for (int j = 0; j < field.subFields.size(); j++) {
                    SubField subfield = subfields.next();
                    bytes[currentAddress++] = SUBFIELD_DELIMITER;
                    bytes[currentAddress++] = (byte) subfield.code;
                    currentAddress = _Encode(bytes, currentAddress, subfield.value, encoding);
                }
            }

            // Ограничитель поля
            bytes[currentAddress++] = FIELD_DELIMITER;
        }

        assert currentAddress == recordLength - 1;

        // Конец записи
        bytes[recordLength - 1] = RECORD_DELIMITER;

        // Собственно запись в поток
        stream.write(bytes);
    }
}
