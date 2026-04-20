package com.monitor.shared.utils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationUtils {

    // Objet Java → tableau de bytes (pour envoyer sur réseau)
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
        }
        return bos.toByteArray();
    }

    // Tableau de bytes → Objet Java (après réception réseau)
    public static Object deserialize(byte[] data)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois =
                new ObjectInputStream(new ByteArrayInputStream(data))) {
            return ois.readObject();
        }
    }
}