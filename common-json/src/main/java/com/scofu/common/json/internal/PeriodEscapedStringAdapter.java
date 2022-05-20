package com.scofu.common.json.internal;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.scofu.common.PeriodEscapedString;
import com.scofu.common.Periods;
import com.scofu.common.json.Adapter;
import java.io.IOException;
import java.lang.reflect.Type;

final class PeriodEscapedStringAdapter implements Adapter<PeriodEscapedString> {

  @Override
  public void write(PeriodEscapedString periodEscapedString, JsonStream jsonStream, Type type)
      throws IOException {
    jsonStream.writeVal(Periods.escape(periodEscapedString.toString()));
  }

  @Override
  public PeriodEscapedString read(JsonIterator jsonIterator, Type type) throws IOException {
    return new PeriodEscapedString(Periods.unescape(jsonIterator.readString()));
  }
}
