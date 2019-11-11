package com.mainacad.util;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component("PasswordEncoder")
public class DefaultPasswordEncoder implements PasswordEncoder {
  private PasswordEncoder passwordEncoder = passwordEncoder();

  @Bean
  private PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(4);
  }

  @Override
  public String encode(CharSequence charSequence) {
    return passwordEncoder.encode(charSequence);
  }

  @Override
  public boolean matches(CharSequence charSequence, String s) {
    return passwordEncoder.matches(charSequence, s);
  }

  @Override
  public boolean upgradeEncoding(String encodedPassword) {
    return passwordEncoder.upgradeEncoding(encodedPassword);
  }
}
