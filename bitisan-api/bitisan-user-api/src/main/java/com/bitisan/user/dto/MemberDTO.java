package com.bitisan.user.dto;

import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.MemberWallet;
import lombok.Data;

import java.util.List;


@Data
public class MemberDTO {

    private Member member ;

    private List<MemberWallet> list ;

}
