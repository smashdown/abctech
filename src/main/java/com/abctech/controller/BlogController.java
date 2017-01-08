package com.abctech.controller;

import com.abctech.model.BTBlogEntry;
import com.abctech.repository.BlogEntryRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/blogs")
public class BlogController {
    private static final int PAGE_ITEM_COUNT = 20;
    private static final Log logger          = LogFactory.getLog(BlogController.class);

    @Autowired private BlogEntryRepository blogEntryRepository;

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public BTBlogEntry addContent(@RequestBody BTBlogEntry blogEntry,
                                  HttpServletResponse response) throws IOException {

        blogEntryRepository.save(blogEntry);
        return blogEntry;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public List<BTBlogEntry> getContentList(@RequestParam("sort") String sort,
                                            @RequestParam("offset") int offset,
                                            HttpServletResponse response) throws IOException {

        logger.info("getContentList() - offset=" + offset);

        PageRequest pageRequest = null;

        if (sort.equals("createdDate")) {
            pageRequest = new PageRequest(offset / PAGE_ITEM_COUNT, PAGE_ITEM_COUNT, Sort.Direction.DESC, sort);
        } else {
            if (sort.equals("title_asc"))
                pageRequest = new PageRequest(offset / PAGE_ITEM_COUNT, PAGE_ITEM_COUNT, Sort.Direction.ASC, "title");
            else
                pageRequest = new PageRequest(offset / PAGE_ITEM_COUNT, PAGE_ITEM_COUNT, Sort.Direction.DESC, "title");
        }

        return blogEntryRepository.findAll(pageRequest).getContent();
    }

}