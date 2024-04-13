package com.netfliz.netfliz.repository;

import com.netfliz.netfliz.entity.Token;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ITokenRepository extends IBaseRepository<Token, String> {
    @Query(value = """
        select t from Token t inner join User u\s
        on t.user.id = u.id\s
        where u.id = id and (t.expired = false or t.revoked = false)\s
        """)
    List<Token> findAllValidTokenByUser(String id);

    @Query("{'token': ?0}")
    Optional<Token> findByToken(String token);
}
