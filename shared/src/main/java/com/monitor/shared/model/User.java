package com.monitor.shared.model;
import java.io.Serializable;

public record User(
    String id,
    String username,
    String password,
    Role role
) implements Serializable {}