package network.tutoria.com.networkdemo.network.api;

import java.util.HashSet;
import java.util.Iterator;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import network.tutoria.com.networkdemo.network.RequestBuilder;
import network.tutoria.com.networkdemo.network.retrofit.NetworkRequestRetrofitProcessor;

/**
 * Created on 2017/10/10 10:15.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public abstract class RequestBase {

    /*
      *取消并删除之前发起的相同url的请求
     */
    public void cancelPreRequest(@android.support.annotation.NonNull final String url) {
        Flowable.fromIterable(requestBuilders).filter(new Predicate<RequestBuilder>() {
            @Override
            public boolean test(@NonNull RequestBuilder requestBuilder) throws Exception {
                return url.equals(requestBuilder.getUrl());
            }
        }).subscribe(new Consumer<RequestBuilder>() {
            @Override
            public void accept(RequestBuilder requestBuilder) throws Exception {
                cancelRequest(requestBuilder);
                requestBuilders.remove(requestBuilder);
            }
        });
    }

    /*
     *取消上次发出的请求
     */
    public void cancelRequest(RequestBuilder requestBuilder) {
        NetworkRequestProcessor requestProcessor = NetworkRequestRetrofitProcessor.getInstance();
        requestProcessor.cancelRequest(requestBuilder);
    }

    private HashSet<RequestBuilder> requestBuilders = new HashSet<>();

    protected void addRequestBuiler(RequestBuilder requestBuilder) {
        requestBuilders.add(requestBuilder);
    }

    protected void removeRequestBuiler(RequestBuilder requestBuilder) {
        requestBuilders.remove(requestBuilder);
    }

    public void checkRequest() {
        if (!requestBuilders.isEmpty()) {
            Iterator<RequestBuilder> requestBuilderIterator = requestBuilders.iterator();
            while (requestBuilderIterator.hasNext()) {
                RequestBuilder request = requestBuilderIterator.next();
                if (request.isDone()) {
                    //删除已经完成的请求
                    requestBuilderIterator.remove();
                }
            }
        }
    }


    public void cancleAllRequest() {
        if (!requestBuilders.isEmpty()) {
            for (RequestBuilder request : requestBuilders) {
                if (!request.isDone()) {
                    cancelRequest(request);
                }
            }
            requestBuilders.clear();
        }
    }
}
