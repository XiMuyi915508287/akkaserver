package com.ximuyi.core.user;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.ximuyi.core.command.response.ByteResponse;

public class InnerUser implements IUser{

    private final IUser exUser;
    private final AtomicLong commandId;
    private final ArrayDeque<ByteResponse> byteResponseList;

    public InnerUser(IUser exUser) {
        this.exUser = exUser;
        this.commandId = new AtomicLong();
        this.byteResponseList = new ArrayDeque<>();
    }

    public IUser getExUser() {
        return exUser;
    }

    public long incCommandIdAndGet() {
        return commandId.incrementAndGet();
    }

    public void cacheResponse(ByteResponse byteResponse){

    }

    public List<ByteResponse> getCacheResponse(long uniqueId){
        return Collections.emptyList();
    }

    @Override
    public long getUserId() {
        return exUser.getUserId();
    }

    @Override
    public String getAccount() {
        return exUser.getAccount();
    }
}
