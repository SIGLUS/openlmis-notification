CREATE TABLE email_attachment (
    id uuid NOT NULL PRIMARY KEY,
    notificationmessageid uuid NOT NULL,
    s3bucket VARCHAR(255) NOT NULL,
    s3folder VARCHAR(255) NOT NULL,
    attachmentfilename VARCHAR (255) NOT NULL,
    attachmentfiletype VARCHAR (255) NOT NULL,
    createddate timestamptz DEFAULT now(),
    FOREIGN KEY (notificationmessageid) REFERENCES notification_messages
);
