package com.bitisan.admin.vo;

import com.bitisan.admin.entity.DataDictionary;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/1214:46
 */
@Data
public class DataDictionaryUpdate {
    @NotBlank
    private String value;
    private String comment;

    public DataDictionary transformation(DataDictionary dataDictionary) {
        dataDictionary.setValue(value);
        dataDictionary.setComment(comment);
        return dataDictionary;
    }
}
