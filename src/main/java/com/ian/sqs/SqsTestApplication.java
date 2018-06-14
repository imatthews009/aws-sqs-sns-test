package com.ian.sqs;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.DeleteTopicRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SqsTestApplication {

//	public static void main(String[] args) {
//		SpringApplication.run(SqsTestApplication.class, args);
//	}
	
	public static void main(String[] args) {
        /*
         * Create a new instance of the builder with all defaults (credentials
         * and region) set automatically. For more information, see
         * Creating Service Clients in the AWS SDK for Java Developer Guide.
         */
//		BasicAWSCredentials awsCreds = new BasicAWSCredentials(Credentials.access_key_id, Credentials.secret_access_key);
		
//        final AmazonSQS sqs = AmazonSQSClientBuilder.standard()
//        		.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
//        		.build();
		
		final AmazonSQS sqs = AmazonSQSClientBuilder.standard()
				.withCredentials(new ProfileCredentialsProvider("personal"))
				.build();

        System.out.println("===============================================");
        System.out.println("Getting Started with Amazon SQS Standard Queues");
        System.out.println("===============================================\n");

        try {
        	final String myQueueUrl = "https://sqs.us-east-1.amazonaws.com/864045830744/Test";
//            // Create a queue.
//            System.out.println("Creating a new SQS queue called MyQueue.\n");
//            final CreateQueueRequest createQueueRequest =
//                    new CreateQueueRequest("MyQueue");
//            final String myQueueUrl = sqs.createQueue(createQueueRequest)
//                    .getQueueUrl();

            // List all queues.
            System.out.println("Listing all queues in your account.\n");
            for (final String queueUrl : sqs.listQueues().getQueueUrls()) {
                System.out.println("  QueueUrl: " + queueUrl);
            }
            System.out.println();

//            // Send a message.
//            System.out.println("Sending a message to MyQueue.\n");
//            sqs.sendMessage(new SendMessageRequest(myQueueUrl,
//                    "This is my message text."));

            // Receive messages.
            System.out.println("Receiving messages from MyQueue.\n");
            final ReceiveMessageRequest receiveMessageRequest =
                    new ReceiveMessageRequest(myQueueUrl);
            final List<Message> messages = sqs.receiveMessage(receiveMessageRequest)
                    .getMessages();
            for (final Message message : messages) {
                System.out.println("Message");
                System.out.println("  MessageId:     "
                        + message.getMessageId());
                System.out.println("  ReceiptHandle: "
                        + message.getReceiptHandle());
                System.out.println("  MD5OfBody:     "
                        + message.getMD5OfBody());
                System.out.println("  Body:          "
                        + message.getBody());
                for (final Entry<String, String> entry : message.getAttributes()
                        .entrySet()) {
                    System.out.println("Attribute");
                    System.out.println("  Name:  " + entry
                            .getKey());
                    System.out.println("  Value: " + entry
                            .getValue());
                }
                //sending a text message from SQSqueue
                AmazonSNS snsClient = AmazonSNSClientBuilder.standard()
                		.withCredentials(new ProfileCredentialsProvider("personal"))
                		.build();
                String snsMessage = message.getBody();
                String phoneNumber = "+16502186166";
                sendSMSMessage(snsClient, snsMessage, phoneNumber);
            }
            System.out.println();

            // Delete the message.
            System.out.println("Deleting a message.\n");
            final String messageReceiptHandle = messages.get(0).getReceiptHandle();
            sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl,
                    messageReceiptHandle));
//
//            // Delete the queue.
//            System.out.println("Deleting the test queue.\n");
//            sqs.deleteQueue(new DeleteQueueRequest(myQueueUrl));
        } catch (final AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means " +
                    "your request made it to Amazon SQS, but was " +
                    "rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (final AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means " +
                    "the client encountered a serious internal problem while " +
                    "trying to communicate with Amazon SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
	
	public static void sendSMSMessage(AmazonSNS snsClient, String message, 
			String phoneNumber) {
	        PublishResult result = snsClient.publish(new PublishRequest()
	                        .withMessage(message)
	                        .withPhoneNumber(phoneNumber));
	        System.out.println(result); // Prints the message ID.
	}
	
}
