// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: HawkResponse.proto

package com.bitisan.core.entity;
@SuppressWarnings({"unchecked","unused"})
public final class HawkResponseMessage {
  private HawkResponseMessage() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface CommonResultOrBuilder extends
      // @@protoc_insertion_point(interface_extends:tutorial.CommonResult)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     *操作返回码，默认为200
     * </pre>
     *
     * <code>int32 resultCode = 1;</code>
     */
    int getResultCode();

    /**
     * <pre>
     *操作返回内容，默认为成功
     * </pre>
     *
     * <code>string resultMsg = 2;</code>
     */
    String getResultMsg();
    /**
     * <pre>
     *操作返回内容，默认为成功
     * </pre>
     *
     * <code>string resultMsg = 2;</code>
     */
    com.google.protobuf.ByteString
        getResultMsgBytes();
  }
  /**
   * <pre>
   * [END java_declaration]
   * 通用的返回结果
   * </pre>
   *
   * Protobuf type {@code tutorial.CommonResult}
   */
  public  static final class CommonResult extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:tutorial.CommonResult)
      CommonResultOrBuilder {
    // Use CommonResult.newBuilder() to construct.
    private CommonResult(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private CommonResult() {
      resultCode_ = 0;
      resultMsg_ = "";
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }
    private CommonResult(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!input.skipField(tag)) {
                done = true;
              }
              break;
            }
            case 8: {

              resultCode_ = input.readInt32();
              break;
            }
            case 18: {
              String s = input.readStringRequireUtf8();

              resultMsg_ = s;
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return HawkResponseMessage.internal_static_tutorial_CommonResult_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return HawkResponseMessage.internal_static_tutorial_CommonResult_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              HawkResponseMessage.CommonResult.class, HawkResponseMessage.CommonResult.Builder.class);
    }

    public static final int RESULTCODE_FIELD_NUMBER = 1;
    private int resultCode_;
    /**
     * <pre>
     *操作返回码，默认为200
     * </pre>
     *
     * <code>int32 resultCode = 1;</code>
     */
    public int getResultCode() {
      return resultCode_;
    }

    public static final int RESULTMSG_FIELD_NUMBER = 2;
    private volatile Object resultMsg_;
    /**
     * <pre>
     *操作返回内容，默认为成功
     * </pre>
     *
     * <code>string resultMsg = 2;</code>
     */
    public String getResultMsg() {
      Object ref = resultMsg_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        resultMsg_ = s;
        return s;
      }
    }
    /**
     * <pre>
     *操作返回内容，默认为成功
     * </pre>
     *
     * <code>string resultMsg = 2;</code>
     */
    public com.google.protobuf.ByteString
        getResultMsgBytes() {
      Object ref = resultMsg_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        resultMsg_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (resultCode_ != 0) {
        output.writeInt32(1, resultCode_);
      }
      if (!getResultMsgBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, resultMsg_);
      }
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (resultCode_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, resultCode_);
      }
      if (!getResultMsgBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, resultMsg_);
      }
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof HawkResponseMessage.CommonResult)) {
        return super.equals(obj);
      }
      HawkResponseMessage.CommonResult other = (HawkResponseMessage.CommonResult) obj;

      boolean result = true;
      result = result && (getResultCode()
          == other.getResultCode());
      result = result && getResultMsg()
          .equals(other.getResultMsg());
      return result;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + RESULTCODE_FIELD_NUMBER;
      hash = (53 * hash) + getResultCode();
      hash = (37 * hash) + RESULTMSG_FIELD_NUMBER;
      hash = (53 * hash) + getResultMsg().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static HawkResponseMessage.CommonResult parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static HawkResponseMessage.CommonResult parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static HawkResponseMessage.CommonResult parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static HawkResponseMessage.CommonResult parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static HawkResponseMessage.CommonResult parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static HawkResponseMessage.CommonResult parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static HawkResponseMessage.CommonResult parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static HawkResponseMessage.CommonResult parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static HawkResponseMessage.CommonResult parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static HawkResponseMessage.CommonResult parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static HawkResponseMessage.CommonResult parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static HawkResponseMessage.CommonResult parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(HawkResponseMessage.CommonResult prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * <pre>
     * [END java_declaration]
     * 通用的返回结果
     * </pre>
     *
     * Protobuf type {@code tutorial.CommonResult}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:tutorial.CommonResult)
        HawkResponseMessage.CommonResultOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return HawkResponseMessage.internal_static_tutorial_CommonResult_descriptor;
      }

      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return HawkResponseMessage.internal_static_tutorial_CommonResult_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                HawkResponseMessage.CommonResult.class, HawkResponseMessage.CommonResult.Builder.class);
      }

      // Construct using HawkResponseMessage.CommonResult.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        resultCode_ = 0;

        resultMsg_ = "";

        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return HawkResponseMessage.internal_static_tutorial_CommonResult_descriptor;
      }

      public HawkResponseMessage.CommonResult getDefaultInstanceForType() {
        return HawkResponseMessage.CommonResult.getDefaultInstance();
      }

      public HawkResponseMessage.CommonResult build() {
        HawkResponseMessage.CommonResult result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public HawkResponseMessage.CommonResult buildPartial() {
        HawkResponseMessage.CommonResult result = new HawkResponseMessage.CommonResult(this);
        result.resultCode_ = resultCode_;
        result.resultMsg_ = resultMsg_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.setField(field, value);
      }
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof HawkResponseMessage.CommonResult) {
          return mergeFrom((HawkResponseMessage.CommonResult)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(HawkResponseMessage.CommonResult other) {
        if (other == HawkResponseMessage.CommonResult.getDefaultInstance()) return this;
        if (other.getResultCode() != 0) {
          setResultCode(other.getResultCode());
        }
        if (!other.getResultMsg().isEmpty()) {
          resultMsg_ = other.resultMsg_;
          onChanged();
        }
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        HawkResponseMessage.CommonResult parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (HawkResponseMessage.CommonResult) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int resultCode_ ;
      /**
       * <pre>
       *操作返回码，默认为200
       * </pre>
       *
       * <code>int32 resultCode = 1;</code>
       */
      public int getResultCode() {
        return resultCode_;
      }
      /**
       * <pre>
       *操作返回码，默认为200
       * </pre>
       *
       * <code>int32 resultCode = 1;</code>
       */
      public Builder setResultCode(int value) {

        resultCode_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       *操作返回码，默认为200
       * </pre>
       *
       * <code>int32 resultCode = 1;</code>
       */
      public Builder clearResultCode() {

        resultCode_ = 0;
        onChanged();
        return this;
      }

      private Object resultMsg_ = "";
      /**
       * <pre>
       *操作返回内容，默认为成功
       * </pre>
       *
       * <code>string resultMsg = 2;</code>
       */
      public String getResultMsg() {
        Object ref = resultMsg_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          resultMsg_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <pre>
       *操作返回内容，默认为成功
       * </pre>
       *
       * <code>string resultMsg = 2;</code>
       */
      public com.google.protobuf.ByteString
          getResultMsgBytes() {
        Object ref = resultMsg_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b =
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          resultMsg_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <pre>
       *操作返回内容，默认为成功
       * </pre>
       *
       * <code>string resultMsg = 2;</code>
       */
      public Builder setResultMsg(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }

        resultMsg_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       *操作返回内容，默认为成功
       * </pre>
       *
       * <code>string resultMsg = 2;</code>
       */
      public Builder clearResultMsg() {

        resultMsg_ = getDefaultInstance().getResultMsg();
        onChanged();
        return this;
      }
      /**
       * <pre>
       *操作返回内容，默认为成功
       * </pre>
       *
       * <code>string resultMsg = 2;</code>
       */
      public Builder setResultMsgBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

        resultMsg_ = value;
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }


      // @@protoc_insertion_point(builder_scope:tutorial.CommonResult)
    }

    // @@protoc_insertion_point(class_scope:tutorial.CommonResult)
    private static final HawkResponseMessage.CommonResult DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new HawkResponseMessage.CommonResult();
    }

    public static HawkResponseMessage.CommonResult getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<CommonResult>
        PARSER = new com.google.protobuf.AbstractParser<CommonResult>() {
      public CommonResult parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new CommonResult(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<CommonResult> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<CommonResult> getParserForType() {
      return PARSER;
    }

    public HawkResponseMessage.CommonResult getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_tutorial_CommonResult_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_tutorial_CommonResult_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\022HawkResponse.proto\022\010tutorial\"5\n\014Common" +
      "Result\022\022\n\nresultCode\030\001 \001(\005\022\021\n\tresultMsg\030" +
      "\002 \001(\tB,\n\025com.spark.hawk.entityB\023HawkResp" +
      "onseMessageb\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_tutorial_CommonResult_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_tutorial_CommonResult_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_tutorial_CommonResult_descriptor,
        new String[] { "ResultCode", "ResultMsg", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
