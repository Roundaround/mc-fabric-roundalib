package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.Config;

import java.util.regex.Pattern;

public class StringConfigOption extends ConfigOption<String> {
  protected StringConfigOption(Builder builder) {
    super(builder);
  }

  public static Builder builder(Config config, String id) {
    return new Builder(config, id);
  }

  public static class Builder extends ConfigOption.AbstractBuilder<String, StringConfigOption, Builder> {
    private Integer minLength = null;
    private Integer maxLength = null;
    private Pattern regex = null;

    private Builder(Config config, String id) {
      super(config, id);
    }

    public Builder setMinLength(int minLength) {
      this.minLength = minLength;
      return this;
    }

    public Builder setMaxLength(int maxLength) {
      this.maxLength = maxLength;
      return this;
    }

    public Builder setRegex(Pattern regex) {
      this.regex = regex;
      return this;
    }

    @Override
    protected void validate() {
      super.validate();

      if (this.maxLength != null) {
        this.validators.addFirst((value, option) -> value != null && value.length() <= this.maxLength);
      }
      if (this.minLength != null) {
        this.validators.addFirst((value, option) -> value != null && value.length() >= this.minLength);
      }
      if (this.regex != null) {
        this.validators.addFirst((value, option) -> value != null && this.regex.matcher(value).find());
      }
    }

    @Override
    protected StringConfigOption buildInternal() {
      return new StringConfigOption(this);
    }
  }
}
