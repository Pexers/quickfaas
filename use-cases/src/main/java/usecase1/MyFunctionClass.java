package usecase1;

import quickfaas.resources.storage.BucketQf;
import quickfaas.resources.storage.StorageQf;
import quickfaas.triggers.storage.BlobQf;
import quickfaas.triggers.storage.BucketEventQf;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MyFunctionClass {  // generate-thumbnail

    String bucket2Name = "bucket2thumbnails/thumbnails";

    public void myFunction(BucketEventQf event, BlobQf blob) throws IOException {
        BucketQf bucket1 = StorageQf.newBucket(event.getBucketName());
        byte[] source = bucket1.readBlob(blob.getName());
        byte[] thumbnail = generateThumbnail(source, "jpeg");
        BucketQf bucket2 = StorageQf.newBucket(bucket2Name);
        bucket2.createBlob("thumbnail-" + blob.getName(), thumbnail, "image/jpeg");
    }

    public byte[] generateThumbnail(byte[] source, String type) throws IOException {
        BufferedImage bImg = ImageIO.read(new ByteArrayInputStream(source));
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
        ImageIO.write(bImg.getSubimage(0, 0, bImg.getWidth() / 2, bImg.getHeight()), type, bOutput);
        return bOutput.toByteArray();
    }

}
