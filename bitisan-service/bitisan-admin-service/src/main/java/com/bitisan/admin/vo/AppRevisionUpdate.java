package com.bitisan.admin.vo;

import com.bitisan.admin.entity.AppRevision;
import com.bitisan.constant.Platform;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/2416:48
 */
@Data
public class AppRevisionUpdate{

    private String remark;

    private String version;

    private String downloadUrl;

    private Platform platform;

    //转化
    public AppRevision transformation(AppRevision appRevision) {
        if (StringUtils.isNotBlank(remark)) {
            appRevision.setRemark(remark);
        }
        if (StringUtils.isNotBlank(version)) {
            appRevision.setVersion(version);
        }
        if (StringUtils.isNotBlank(downloadUrl)) {
            appRevision.setDownloadUrl(downloadUrl);
        }
        if (platform != null) {
            appRevision.setPlatform(platform);
        }
        return appRevision;
    }


}
