package ws.aperture.stock.dto;

import java.time.LocalDate;

import ws.aperture.stock.model.*;

public record StockRecordDTO(Long id, LocalDate incomingDate, LocalDate expireDate ,
                            double quantity) {

    public static StockRecordDTO generateDTO(StockRecord stockRecord) {
        return new StockRecordDTO( stockRecord.getId(), stockRecord.getIncomingDate(), 
                                    stockRecord.getExpiryDate(), stockRecord.getQuantity());
    }
                                
}
