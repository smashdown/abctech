package com.abctech.controller;

import com.abctech.service.ImageService;
import com.mongodb.gridfs.GridFSDBFile;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/images")
public class ImageController {
    private static final Log logger = LogFactory.getLog(ImageController.class);

    @Autowired GridFsTemplate gfsTemplate;
    @Autowired ImageService   imageService;

    @PostMapping("")
    @ResponseBody
    public String postImage(
            @RequestParam("file") MultipartFile file,
            HttpServletResponse response) throws IOException {

        logger.info("postImage() file name=" + file.getOriginalFilename());

        return imageService.storeImage(file, response);
    }

    @RequestMapping(value = "/{image_id}", headers = "Accept=image/jpeg, image/jpg, image/png, image/gif, image/bmp", method = RequestMethod.GET)
    public HttpEntity<byte[]> getImage(@PathVariable("image_id") String imageId,
                                       HttpServletResponse response) throws IOException {
        logger.info("getImage() imageId=" + imageId);

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(imageId));

        GridFSDBFile file = gfsTemplate.findOne(query);

        if (file != null) {
            byte[] bytes = IOUtils.toByteArray(file.getInputStream());
            // send it back to the client
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(file.getContentType()));
            return new ResponseEntity<byte[]>(bytes, httpHeaders, HttpStatus.OK);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }
}