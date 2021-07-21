/*
 * Copyright 2017-2020 original authors
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

import com.mongodb.DBObjectCodecProvider;
import com.mongodb.DBRefCodecProvider;
import com.mongodb.DocumentToDBRefTransformer;
import com.mongodb.client.gridfs.codecs.GridFSFileCodecProvider;
import com.mongodb.client.model.geojson.codecs.GeoJsonCodecProvider;
import io.micronaut.configuration.mongo.core.DefaultMongoConfiguration;
import io.micronaut.configuration.mongo.core.MongoSettings;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.runtime.ApplicationConfiguration;
import org.bson.codecs.BsonCodecProvider;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.IterableCodecProvider;
import org.bson.codecs.MapCodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.jsr310.Jsr310CodecProvider;

import java.util.List;

import static java.util.Arrays.asList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

/**
 * Overridden default MongoDB configuration class that replaces default codecs by the AWS DocDB specific ones.
 */
@Requires(property = MongoSettings.PREFIX)
@Requires(missingProperty = MongoSettings.MONGODB_SERVERS)
@ConfigurationProperties(MongoSettings.PREFIX)
@Replaces(DefaultMongoConfiguration.class)
public class AwsDocDbDefaultMongoConfiguration extends DefaultMongoConfiguration {

    /**
     * This is the copy of {@link com.mongodb.MongoClientSettings#getDefaultCodecRegistry()} with replaced
     * {@link org.bson.codecs.ValueCodecProvider} by {@link AwsDocDbValueCodecProvider}.
     */
    private static final CodecRegistry DEFAULT_CODEC_REGISTRY =
            fromProviders(asList(new AwsDocDbValueCodecProvider(),
                    new BsonValueCodecProvider(),
                    new DBRefCodecProvider(),
                    new DBObjectCodecProvider(),
                    new DocumentCodecProvider(new DocumentToDBRefTransformer()),
                    new IterableCodecProvider(new DocumentToDBRefTransformer()),
                    new MapCodecProvider(new DocumentToDBRefTransformer()),
                    new GeoJsonCodecProvider(),
                    new GridFSFileCodecProvider(),
                    new Jsr310CodecProvider(),
                    new BsonCodecProvider()));


    public AwsDocDbDefaultMongoConfiguration(ApplicationConfiguration applicationConfiguration) {
        super(applicationConfiguration);
    }

    public AwsDocDbDefaultMongoConfiguration(ApplicationConfiguration applicationConfiguration, Environment environment) {
        super(applicationConfiguration, environment);
    }

    @Override
    protected void addDefaultCodecRegistry(List<CodecRegistry> codecRegistries) {
        codecRegistries.add(DEFAULT_CODEC_REGISTRY);
    }
}
