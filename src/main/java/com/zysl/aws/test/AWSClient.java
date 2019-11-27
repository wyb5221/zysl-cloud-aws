package com.zysl.aws.test;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * init()：通过default方法连接aws；
 * init_with_key()：通过在代码中包含 access key id 和 secret access key 连接 aws；
 * createBucket(String bucketName)：用于创建一个名为bucketName的s3 bucket；
 * private static void listObjects(String bucketName)：列出名为bucketName的bucket里面的所有object(object相当于所有文件和文件夹)；
 * isObjectExit(String bucketName, String key)：判断名为bucketName的bucket里面是否有一个名为key的object；
 * createSampleFile(String bucketName, String filename)：在本地创建一个名为filename的文件，输入一些信息后，将其传到名为bucketName的s3 bucket里面去；
 * showContentOfAnObject(String bucketName, String key)：输出"s3://"+bucketname+"/"key对应的object对应的信息；
 * showContentOfAnGzipObject(String bucketName, String key)：输出"s3://"+bucketname+"/"key对应的object对应的信息(这个object对应的文件时gzip格式的)；
 * listBuckets()：列出我的s3上所有的bucket的名字；
 * deleteBucket(String bucketName)：删除一个名为bucketName的bucket；
 * deleteObjectsWithPrefix(String bucketName, String prefix)：删除名为bucketName的bucket里面所有key的前缀和prefix匹配的object；
 * uploadFileToBucket(String path, String bucketName)：上传一个本地文件(对应位置为path)上传到名为bucketName的bucket；
 * createFolder(String bucketName, String folderName)：在aws上创建一个目录（这个函数上网查了两天才找到方法，很重要！）。
 */
public class AWSClient {

    static AmazonS3 s3;
    static TransferManager tx;

    private static String AWS_ACCESS_KEY = "AKIASLXIN4C6IQPCP3N2";
    private static String AWS_SECRET_KEY = "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";

    static final String bucketName = "test-wyb";

    private static void init() throws Exception {
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException("Exception on credentials.", e);
        }
        s3 = new AmazonS3Client(credentials);
//        Region usWest2 = Region.getRegion(Regions.US_WEST_2);
        //s3.setRegion(usWest2);
        tx = new TransferManager(s3);
    }

    private static void init_with_key() throws Exception {
        AWSCredentials credentials = null;
        credentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
        s3 = new AmazonS3Client(credentials);
        //Region usWest2 = Region.getRegion(Regions.US_WEST_2);
        //s3.setRegion(usWest2);
        tx = new TransferManager(s3);

//        s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();

    }

    private static void createBucket(String bucketName) {
//        if(s3.doesBucketExist(bucketName) == true) {
//            System.out.println(bucketName + " already exist!");
//            return;
//        }
        System.out.println("creating " + bucketName + " ...");
        s3.createBucket(bucketName);
        System.out.println(bucketName + " has been created!");
    }

    private static void listObjects(String bucketName) {
        System.out.println("Listing objects of " + bucketName);
        ObjectListing objectListing = s3.listObjects(bucketName);
        int objectNum = 0;
        for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            System.out.println(" - " + objectSummary.getKey());
            objectNum ++;
        }
        System.out.println("total " + objectNum + " object(s).");
    }

    private static boolean isObjectExit(String bucketName, String key) {
        int len = key.length();
        ObjectListing objectListing = s3.listObjects(bucketName);
        String s = new String();
        for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            s = objectSummary.getKey();
            int slen = s.length();
            if(len == slen) {
                int i;
                for(i=0;i<len;i++) if(s.charAt(i) != key.charAt(i)) break;
                if(i == len) return true;
            }
        }
        return false;
    }

    private static void createSampleFile(String bucketName, String filename) throws IOException {
        if(isObjectExit(bucketName, filename) == true) {
            System.out.println(filename +" already exists in " + bucketName + "!");
            return;
        }
        System.out.println("creating file " + filename);
        File file = new File(filename);
        file.deleteOnExit();

        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.write("01234567890112345678901234\n");
        writer.write("!@#$%^&*()-=[]{};':',.<>/?\n");
        writer.write("01234567890112345678901234\n");
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.close();

        s3.putObject(bucketName, filename, file);
        System.out.println("create sample file " + filename + " succeed!");
    }

    private static void showContentOfAnObject(String bucketName, String key) {
        S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
        InputStream input = object.getObjectContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                System.out.println("    " + line);
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showContentOfAnGzipObject(String bucketName, String key) {
        try {
            S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
            InputStream input = new GZIPInputStream(object.getObjectContent());
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                System.out.println("    " + line);
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void listBuckets() {
        System.out.println("Listing buckets");
        int bucketNum = 0;
        for(Bucket bucket : s3.listBuckets()) {
            System.out.println(" - " + bucket.getName());
            bucketNum ++;
        }
        System.out.println("total " + bucketNum + " bucket(s).");
    }

    private static void deleteBucket(String bucketName) {
        if(s3.doesBucketExist(bucketName) == false) {
            System.out.println(bucketName + " does not exists!");
            return;
        }
        System.out.println("deleting " + bucketName + " ...");
        ObjectListing objectListing = s3.listObjects(bucketName);
        for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            String key = objectSummary.getKey();
            s3.deleteObject(bucketName, key);
        }
        s3.deleteBucket(bucketName);
        System.out.println(bucketName + " has been deleted!");
    }

    private static void deleteObjectsWithPrefix(String bucketName, String prefix) {
        if(s3.doesBucketExist(bucketName) == false) {
            System.out.println(bucketName + " does not exists!");
            return;
        }
        System.out.println("deleting " + prefix +"* in " + bucketName + " ...");
        int pre_len = prefix.length();
        ObjectListing objectListing = s3.listObjects(bucketName);
        for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            String key = objectSummary.getKey();
            int len = key.length();
            if(len < pre_len) continue;
            int i;
            for(i=0;i<pre_len;i++)
                if(key.charAt(i) != prefix.charAt(i))
                    break;
            if(i < pre_len) continue;
            s3.deleteObject(bucketName, key);
        }
        System.out.println("All " + prefix + "* deleted!");
    }

    private static void uploadFileToBucket(String path, String bucketName) {
        File fileToUpload = new File(path);
        if(fileToUpload.exists() == false) {
            System.out.println(path + " not exists!");
            return;
        }
        PutObjectRequest request = new PutObjectRequest(bucketName, fileToUpload.getName(), fileToUpload);
        Upload upload = tx.upload(request);
        while((int)upload.getProgress().getPercentTransferred() < 100) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println(path + " upload succeed!");
    }

    private static void createFolder(String bucketName, String folderName) {
        // Create metadata for my folder & set content-length to 0
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        // Create empty content
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        // Create a PutObjectRequest passing the foldername suffixed by /
        PutObjectRequest putObjectRequest =
                new PutObjectRequest(bucketName, folderName+"/",
                        emptyContent, metadata);
        // Send request to S3 to create folder
        s3.putObject(putObjectRequest);
    }

    public static void main(String[] args) throws Exception {

//        init();
        init_with_key();
        System.out.println("----s3："+s3);
        createBucket("test-w1");
        listBuckets();
//        showContentOfAnGzipObject(bucketName, "testfile.gz");


        System.exit(0);
    }
}
