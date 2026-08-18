package com.hei.note.service.event;

import com.hei.note.endpoint.event.model.TranscriptEmailRequested;
import com.hei.note.service.TranscriptDeliveryService;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TranscriptEmailRequestedService implements Consumer<TranscriptEmailRequested> {

  private final TranscriptDeliveryService transcriptDeliveryService;

  @Override
  public void accept(TranscriptEmailRequested event) {
    transcriptDeliveryService.processTranscriptEmail(event.getTranscriptId(), event.getRecipientEmail());
  }
}
