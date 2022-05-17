package com.scofu.common.json.lazy;

import com.jsoniter.any.Any;

/** Represents a lazy thing. */
public interface Lazy {

  /** Returns the internal json representation. */
  Any any();
}
