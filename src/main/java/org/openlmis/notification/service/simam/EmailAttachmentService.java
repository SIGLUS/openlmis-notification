/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.notification.service.simam;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.IOException;
import javax.activation.DataSource;
import javax.annotation.PostConstruct;
import javax.mail.util.ByteArrayDataSource;
import org.openlmis.notification.domain.EmailAttachment;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailAttachmentService {
  protected static final XLogger XLOGGER = XLoggerFactory.getXLogger(EmailAttachmentService.class);

  public static final String FOLDER_SUFFIX = "/";

  @Value("${aws.access.key}")
  private String accessKey;
  @Value("${aws.secret.access.key}")
  private String secretKey;
  @Value("${aws.region}")
  private String region;

  private AWSCredentials credentials;
  private AmazonS3 s3Client;

  @PostConstruct
  public void init() {

    credentials = new BasicAWSCredentials(accessKey, secretKey);
    s3Client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(region)
            .build();
  }

  public DataSource getAttatchmentDataSource(EmailAttachment emailAttachment) {
    String bucketName = emailAttachment.getS3Bucket();
    String keyName = emailAttachment.getS3Folder() + FOLDER_SUFFIX
        + emailAttachment.getAttachmentFileName();
    S3Object s3Object = s3Client.getObject(bucketName, keyName);
    S3ObjectInputStream inputStream = s3Object.getObjectContent();
    DataSource attachmentDataSource = null;
    try {
      attachmentDataSource = new ByteArrayDataSource(inputStream.getDelegateStream(),
          emailAttachment.getAttachmentFileType());
    } catch (IOException e) {
      XLOGGER.error(e.getMessage());
    }

    return attachmentDataSource;
  }
}