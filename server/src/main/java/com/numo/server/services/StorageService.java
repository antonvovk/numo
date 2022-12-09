package com.numo.server.services;

public interface StorageService {

    String putObject(String name, byte[] content);
}
