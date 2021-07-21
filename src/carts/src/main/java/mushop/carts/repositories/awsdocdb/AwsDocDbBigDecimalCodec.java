/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mushop.carts.repositories.awsdocdb;


import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.math.BigDecimal;

/**
 * Specific BigDecimal codec for AWS DocDbCodec that stores values to double instead of Decimal128 type.
 *
 * @see <a href="https://docs.aws.amazon.com/documentdb/latest/developerguide/mongo-apis.html>mongo apis</a>
 */
public class AwsDocDbBigDecimalCodec implements Codec<BigDecimal> {

    @Override
    public BigDecimal decode(BsonReader bsonReader, DecoderContext decoderContext) {
        return BigDecimal.valueOf(bsonReader.readDouble());
    }

    @Override
    public void encode(BsonWriter bsonWriter, BigDecimal bigDecimal, EncoderContext encoderContext) {
        bsonWriter.writeDouble(bigDecimal.doubleValue());
    }

    @Override
    public Class<BigDecimal> getEncoderClass() {
        return BigDecimal.class;
    }
}
