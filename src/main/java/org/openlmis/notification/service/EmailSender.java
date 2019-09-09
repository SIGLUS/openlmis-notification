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

package org.openlmis.notification.service;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.openlmis.notification.i18n.MessageKeys.ERROR_SEND_MAIL_FAILURE;

import java.util.List;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.openlmis.notification.domain.EmailAttachment;
import org.openlmis.notification.service.simam.EmailAttachmentService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
class EmailSender {

  private static final XLogger XLOGGER = XLoggerFactory.getXLogger(EmailSender.class);

  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  private EmailAttachmentService emailAttachmentService;

  @Value("${email.noreply}")
  private String from;

  static {
    System.setProperty("mail.mime.splitlongparameters", "false");
  }

  void sendMail(String to, String subject, String body, Boolean isHtml,
            List<EmailAttachment> emailAttachments) {
    XLOGGER.entry(to, subject, body, isHtml);
    Profiler profiler = new Profiler("SEND_MAIL");
    profiler.setLogger(XLOGGER);

    try {
      profiler.start("CREATE_MAIL_MESSAGE");
      MimeMessage mailMessage = mailSender.createMimeMessage();

      profiler.start("CREATE_MESSAGE_HELPER");
      Boolean multipart = isEmpty(emailAttachments) ? false : true;
      MimeMessageHelper helper = new MimeMessageHelper(mailMessage, multipart);
      helper.setFrom(from);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(body, isHtml);

      if (!isEmpty(emailAttachments)) {
        emailAttachments.forEach(attachment -> {
          DataSource dataSource = emailAttachmentService.getAttatchmentDataSource(attachment);
          try {
            helper.addAttachment(attachment.getAttachmentFileName(), dataSource);
          } catch (MessagingException e) {
            XLOGGER.error(e.getMessage());
          }
        });
      }

      profiler.start("SEND_MESSAGE");
      mailSender.send(mailMessage);

      profiler.stop().log();
      XLOGGER.exit();
    } catch (Exception exp) {
      ServerException exception = new ServerException(exp, ERROR_SEND_MAIL_FAILURE);

      XLOGGER.throwing(exception);
      profiler.stop().log();

      throw exception;
    }
  }

  void sendMail(String to, String subject, String body) {
    Boolean isHtml = false;
    sendMail(to, subject, body, isHtml, null);
  }
}
