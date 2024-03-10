package com.bitisan.admin.vo;

import com.bitisan.admin.entity.DataDictionary;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/1214:24
 */
@Data
public class DataDictionaryCreate {
    @NotBlank
    private String bond;
    @NotBlank
    private String value;
    private String comment;


    public DataDictionary transformation() {
        return new DataDictionary(bond, value, comment);
    }
}
