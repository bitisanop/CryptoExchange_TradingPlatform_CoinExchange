package com.bitisan.admin.vo;

import com.bitisan.admin.entity.AppRevision;
import com.bitisan.constant.Platform;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/2416:33
 */
@Data
public class AppRevisionCreate {

    private String remark;

    @NotBlank
    private String version;

    private String downloadUrl;

    @NotNull
    private Platform platform;

    //转化
    public AppRevision transformation() {
        AppRevision appRevision = new AppRevision();
        appRevision.setRemark(this.remark);
        appRevision.setVersion(this.version);
        appRevision.setDownloadUrl(this.downloadUrl);
        appRevision.setPlatform(this.platform);
        return appRevision;
    }
}
