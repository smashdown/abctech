package com.abctech.service;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class ImageService {
    private static final String META_KEY_SIZE   = "size";
    private static final String META_KEY_PARENT = "parent";

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    @Autowired private GridFsTemplate gfsTemplate;

    public boolean validate(MultipartFile file, HttpServletResponse response) throws IOException {
        if (file == null || file.getSize() < 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File size is zero");
            return false;
        }
        logger.info("contentType=" + file.getContentType());

        if (!file.getContentType().toLowerCase().contains("image")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported media type");
            return false;
        }
        return true;
    }

    public String storeImage(MultipartFile file, HttpServletResponse response) throws IOException {
        if (!validate(file, response)) {
            return null;
        }

        final String imageFormat = file.getContentType().split("/")[1].toLowerCase();
        logger.info("image format=" + imageFormat);

        Map<String, String> metaData = new HashMap<>();
        metaData.put(META_KEY_SIZE, "org");
        metaData.put(META_KEY_PARENT, "self");

        /* store org image first */
        GridFSFile fileOrg = gfsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), metaData);

        /* normal facebook - 200X200 */
        metaData.put(META_KEY_SIZE, "normal");
        metaData.put(META_KEY_PARENT, fileOrg.getId().toString());

        /* large facebook - 414X414 */
        metaData.put(META_KEY_SIZE, "large");
        metaData.put(META_KEY_PARENT, fileOrg.getId().toString());

        BufferedImage orgImage = ImageIO.read(file.getInputStream());
        BufferedImage largeImage = Scalr.resize(orgImage,
                Scalr.Method.BALANCED,
                Scalr.Mode.FIT_TO_WIDTH,
                256);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(largeImage, imageFormat, os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        fileOrg = gfsTemplate.store(is, file.getOriginalFilename(), file.getContentType(), metaData);

        return fileOrg.getId().toString();
    }

    public GridFSDBFile getImage(String imageId, String imageSize) {
        Query query = new Query();
        Criteria criteria = Criteria.where("metadata.size").is(imageSize).orOperator(Criteria.where("_id").is(imageId), Criteria.where("metadata.parent").is(imageId));
        query.addCriteria(criteria);

        return gfsTemplate.findOne(query);
    }
}