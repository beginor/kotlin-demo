package cn.beginor.demo.security

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import jakarta.annotation.PostConstruct
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*

@Component
class JwtUtil {

    companion object ClaimNames {
        const val USERNAME: String = "USERNAME"
        const val AUTHORITIES: String = "AUTHORITIES"
    }

    val sharedKey = ByteArray(32)

    @PostConstruct
    fun init() {
        val random = SecureRandom()
        random.nextBytes(sharedKey)
    }

    fun generateToken(user: UserDetails, issuer: String): String? {
        val authorities = user.authorities.map { it.authority }
        val claimsSet = JWTClaimsSet.Builder()
            .jwtID("0")
            .claim(USERNAME, user.username)
            .claim(AUTHORITIES, authorities)
            .issuer(issuer)
            .expirationTime(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
            .build()
        return generateToken(claimsSet)
    }

    fun generateToken(claims: JWTClaimsSet): String? {
        return try {
            val header = JWSHeader(JWSAlgorithm.HS256)
            val payload = Payload(claims.toJSONObject())
            val jwsObject = JWSObject(header, payload)
            jwsObject.sign(MACSigner(sharedKey))
            jwsObject.serialize()
        }
        catch (ex: Exception) {
            return null
        }
    }

    fun parseToken(token: String): JWTClaimsSet? {
        return try {
            val jwsObject = JWSObject.parse(token)
            if (jwsObject.verify(MACVerifier(sharedKey))) {
                JWTClaimsSet.parse(jwsObject.payload.toJSONObject())
            } else {
                null
            }
        } catch (ex: Exception) {
            null
        }
    }

    fun parseToken(token: String, issuer: String): AbstractAuthenticationToken? {
        val claimsSet = parseToken(token) ?: return null
        if (claimsSet.expirationTime.time < System.currentTimeMillis()) {
            return null
        }
        if (!claimsSet.issuer.equals(issuer, true)) {
            return null
        }
        val username = claimsSet.getStringClaim(USERNAME)
        val roles = claimsSet.getStringArrayClaim(AUTHORITIES).map { SimpleGrantedAuthority(it) }
        return UsernamePasswordAuthenticationToken(username, null, roles)
    }

}
