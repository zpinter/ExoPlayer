/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer.extractor.ogg;

import com.google.android.exoplayer.testutil.FakeExtractorInput;
import com.google.android.exoplayer.testutil.TestUtil;

import junit.framework.TestCase;

import java.io.IOException;

/**
 * Unit test for {@link OggExtractor}.
 */
public final class OggExtractorTest extends TestCase {

  private OggExtractor extractor;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    extractor = new OggExtractor();
  }

  public void testSniffVorbis() throws Exception {
    byte[] data = TestUtil.joinByteArrays(
        TestData.buildOggHeader(0x02, 0, 1000, 1),
        TestUtil.createByteArray(7),  // Laces
        new byte[] {0x01, 'v', 'o', 'r', 'b', 'i', 's'});
    assertTrue(sniff(data));
  }

  public void testSniffFlac() throws Exception {
    byte[] data = TestUtil.joinByteArrays(
        TestData.buildOggHeader(0x02, 0, 1000, 1),
        TestUtil.createByteArray(5),  // Laces
        new byte[] {0x7F, 'F', 'L', 'A', 'C'});
    assertTrue(sniff(data));
  }

  public void testSniffFailsOpusFile() throws Exception {
    byte[] data = TestUtil.joinByteArrays(
        TestData.buildOggHeader(0x02, 0, 1000, 0x00),
        new byte[] {'O', 'p', 'u', 's'});
    assertFalse(sniff(data));
  }

  public void testSniffFailsInvalidOggHeader() throws Exception {
    byte[] data = TestData.buildOggHeader(0x00, 0, 1000, 0x00);
    assertFalse(sniff(data));
  }

  public void testSniffInvalidHeader() throws Exception {
    byte[] data = TestUtil.joinByteArrays(
        TestData.buildOggHeader(0x02, 0, 1000, 1),
        TestUtil.createByteArray(7),  // Laces
        new byte[] {0x7F, 'X', 'o', 'r', 'b', 'i', 's'});
    assertFalse(sniff(data));
  }

  public void testSniffFailsEOF() throws Exception {
    byte[] data = TestData.buildOggHeader(0x02, 0, 1000, 0x00);
    assertFalse(sniff(data));
  }

  private boolean sniff(byte[] data) throws InterruptedException, IOException {
    FakeExtractorInput input = new FakeExtractorInput.Builder().setData(data)
        .setSimulateIOErrors(true).setSimulateUnknownLength(true).setSimulatePartialReads(true)
        .build();
    return TestUtil.sniffTestData(extractor, input);
  }

}
