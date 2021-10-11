package org.adv.alg.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.adv.alg.service.ResumeProcessor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping(path = "/resume")
public class ResumeProcessController {

  private ObjectMapper objectMapper;
  private ResumeProcessor resumeProcessor;

  public ResumeProcessController(
      ObjectMapper objectMapper, ResumeProcessor resumeProcessor) {
    this.objectMapper = objectMapper;
    this.resumeProcessor = resumeProcessor;
  }

  @PostMapping(
      value = "/process",
      produces = {MediaType.APPLICATION_JSON_VALUE},
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @ApiOperation(
      value = "process word format resume",
      notes = "process word format resume")
  @ApiResponses({
      @ApiResponse(code = 200, message = "Successfully process word format resume"),
      @ApiResponse(code = 422, message = "Invalid Input supplied or input parameters missing"),
      @ApiResponse(code = 500, message = "There is a problem internally to process the resume")
  })
  public ResponseEntity<String> processResume(
      @RequestParam("file") MultipartFile file,
      @RequestParam("keyWord") String keyWord)
      throws IOException, OpenXML4JException, XmlException, InterruptedException, ExecutionException, TimeoutException {
    String content = resumeProcessor.processResume(file, keyWord);
    return new ResponseEntity<>(content, HttpStatus.OK);
  }

  @PostMapping(
          value = "/processWithBMOnly",
          produces = {MediaType.APPLICATION_JSON_VALUE},
          consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @ApiOperation(
          value = "process word format resume",
          notes = "process word format resume")
  @ApiResponses({
          @ApiResponse(code = 200, message = "Successfully process word format resume"),
          @ApiResponse(code = 422, message = "Invalid Input supplied or input parameters missing"),
          @ApiResponse(code = 500, message = "There is a problem internally to process the resume")
  })
  public ResponseEntity<String> processWithBMOnly(
          @RequestParam("file") MultipartFile file,
          @RequestParam("keyWord") String keyWord)
          throws IOException, OpenXML4JException, XmlException, InterruptedException, ExecutionException, TimeoutException {
    String content = resumeProcessor.processResumeWithBMOnly(file, keyWord);
    return new ResponseEntity<>(content, HttpStatus.OK);
  }

  @PostMapping(
      value = "/process2",
      consumes = {MediaType.APPLICATION_JSON_VALUE},
      produces = {MediaType.APPLICATION_JSON_VALUE})
  @ApiOperation(value = "process word format resume")
  @ApiResponses({
      @ApiResponse(code = 200, message = "Successfully process word format resume"),
      @ApiResponse(code = 422, message = "Invalid Input supplied or input parameters missing"),
      @ApiResponse(code = 500, message = "There is a problem internally to process the resume")
  })
  public ResponseEntity<?> processResume2(@RequestBody String input) {

    return new ResponseEntity<>("done"+input, HttpStatus.OK);
  }

}
