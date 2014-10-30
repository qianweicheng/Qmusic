package com.qmusic.common;

public interface IAsyncDataCallback<T> {

	void callback(int result, T data);
}
