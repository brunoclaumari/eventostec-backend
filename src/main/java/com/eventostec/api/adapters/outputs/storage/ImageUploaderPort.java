package com.eventostec.api.adapters.outputs.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploaderPort {

    String uploadImg(MultipartFile multipartFile);
}
