package com.bitisan.user.transform;

import com.alibaba.fastjson.JSON;
import com.bitisan.user.entity.Member;
import com.bitisan.user.vo.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author Bitisan  E-mail:xunibidev@gmail.com
 * @date 2020年01月11日
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthMember implements Serializable {
    private static final long serialVersionUID = -4199550203850153635L;
    private long id;
    private String name;
    private String realName;
    private Location location;
    private String mobilePhone;
    private String email;
    private Integer memberLevel;
    private Integer status;

    /**
     * 如需添加信息在{@link #toAuthMember(Member)}方法中添加
     *
     * @param member
     * @return
     */
    public static AuthMember toAuthMember(Member member) {
        Location location = new Location();
        location.setCity(member.getCity());
        location.setCountry(member.getCountry());
        location.setProvince(member.getProvince());
        location.setDistrict(member.getDistrict());
        return AuthMember.builder()
                .id(member.getId())
                .name(member.getUsername())
                .realName(member.getRealName())
                .location(location)
                .mobilePhone(member.getMobilePhone())
                .email(member.getEmail())
                .memberLevel(member.getMemberLevel())
                .status(member.getStatus())
                .build();
    }

    public static AuthMember toAuthMember(String json)  {
        try {
            return JSON.parseObject(URLDecoder.decode(json,"UTF-8"),AuthMember.class);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
