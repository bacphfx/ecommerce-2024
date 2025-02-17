package com.ecommerce.admin.product;

import com.ecommerce.admin.FileUploadUtil;
import com.ecommerce.common.entity.product.Product;
import com.ecommerce.common.entity.product.ProductImage;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class ProductSaveHelper {
    static void deleteImagesRemoved(Product product) {
        String extraImageDir = "../product-images/" + product.getId() + "/extras";
        Path dirPath = Paths.get(extraImageDir);
        try {
            Files.list(dirPath).forEach(file->{
                String fileName = file.toFile().getName();
                if(!product.containsImageName(fileName)){
                    try {
                        Files.delete(file);
                        System.out.println("Deleted image");
                    } catch (IOException e) {
                        System.out.println("Could not delete extra image: " + fileName);
                    }
                }
            });
        } catch (IOException e) {
            System.out.println("Could not list directory: " + dirPath);
        }
    }

    static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
        if (imageIDs == null || imageIDs.length == 0) return;
        Set<ProductImage> images = new HashSet<>();
        for (int i = 0; i < imageIDs.length; i++){
            Integer id = Integer.parseInt(imageIDs[i]);
            String name = imageNames[i];
            images.add(new ProductImage(id, name, product));
        }
        product.setImages(images);
    }

    static void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
        if (detailNames == null || detailNames.length == 0) return;
        for (int i = 0; i < detailNames.length; i++) {
            String name = detailNames[i];
            String value = detailValues[i];
            Integer id = Integer.parseInt(detailIDs[i]);

            if (id != 0){
                product.addDetails(id, name,value);
            } else if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetails(name, value);
            }
        }
    }

    static void saveUploadedImages(MultipartFile mainImageMultipart,
                                    MultipartFile[] extraImageMultiparts,
                                    Product savedProduct) throws IOException {
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            String uploadDir = "../product-images/" + savedProduct.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
        }

        if (extraImageMultiparts.length > 0) {
            String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (multipartFile.isEmpty()) continue;
                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

            }
        }
    }

    static void setMainImageName(MultipartFile mainImageMultipart, Product product) {
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }

    static void setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
        if (extraImageMultiparts.length > 0) {
            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    if (!product.containsImageName(fileName)) {
                        product.addExtraImage(fileName);
                    }
                }
            }
        }
    }
}
