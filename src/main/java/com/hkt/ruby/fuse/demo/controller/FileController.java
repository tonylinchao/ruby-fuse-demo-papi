package com.hkt.ruby.fuse.demo.controller;

import com.hkt.ruby.fuse.demo.utils.R;
import com.hkt.ruby.fuse.demo.utils.ResultCode;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Demo REST API of Streaming File Transfer
 *
 * @author Tony C Lin
 */
@RestController
@RequestMapping("/file")
public class FileController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FluentProducerTemplate fluentProducerTemplate;

    /**
     * Get File via Streaming
     *
     * @param endpoint Endpoint Name
     * @param fileName File Name
     */
    @GetMapping(path="/stream/{endpoint}")
    public R getStreamFile(@PathVariable(value="endpoint",required=true) String endpoint,
                           @RequestParam(value="fileName",required=false) String fileName,
                           @RequestParam(value="outputFile",required=false) String outputFile) {

        logger.debug("Stream request is [" + endpoint + "],[" + fileName +"], [" + outputFile + "]");
        Exchange result = fluentProducerTemplate
                .withHeader("endpoint", endpoint)
                .withHeader("fileName", fileName)
                .withHeader("outputFile", outputFile)
                .to("direct:file-stream").send();

        return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
    }

}
