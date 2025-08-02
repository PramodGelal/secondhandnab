package com.Six_sem_project.PSR;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    public String uploadImage(File file) throws IOException {
        try {
            Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            e.printStackTrace();  // Add this
            throw new IOException("Cloudinary upload failed: " + e.getMessage());
        }
    }

}
