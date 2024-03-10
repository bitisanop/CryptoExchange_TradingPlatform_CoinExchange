package com.bitisan.admin.dao;


import com.bitisan.admin.entity.MemberLog;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface MemberLogDao extends MongoRepository<MemberLog,Long> {
}
