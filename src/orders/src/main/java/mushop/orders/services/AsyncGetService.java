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
package mushop.orders.services;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClientConfiguration;
import io.micronaut.http.client.RxHttpClientFactory;
import io.reactivex.Flowable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;


@Singleton
public class AsyncGetService {

    @Inject
    private RxHttpClientFactory httpClientFactory;

    @Inject
    private HttpClientConfiguration httpClientConfiguration;

    public <T> Flowable<T> getObject(URI uri, Class<T> responseClass) throws MalformedURLException {
        return httpClientFactory.createClient(uri.toURL(), httpClientConfiguration)
                .retrieve(HttpRequest.GET(""), responseClass);
    }

    public <T> Flowable<List<T>> getDataList(URI uri, Class<T> responseClass) throws MalformedURLException {
        return httpClientFactory.createClient(uri.toURL(), httpClientConfiguration)
                .retrieve(HttpRequest.GET(""), Argument.listOf(responseClass));
    }
}
