package com.construction.management.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "work_items")
public class WorkItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "quantity", nullable = false, precision = 14, scale = 3)
    private BigDecimal quantity;

    @Column(name = "price", nullable = false, precision = 14, scale = 2)
    private BigDecimal price;

    @Column(name = "transport_vehicle_no", length = 50)
    private String transportVehicleNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    public WorkItem() {
    }

    public WorkItem(Site site, String itemName, BigDecimal quantity, BigDecimal price,
                    String transportVehicleNo, Vendor vendor, LocalDate entryDate) {
        this.site = site;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.transportVehicleNo = transportVehicleNo;
        this.vendor = vendor;
        this.entryDate = entryDate;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getTransportVehicleNo() {
        return transportVehicleNo;
    }

    public void setTransportVehicleNo(String transportVehicleNo) {
        this.transportVehicleNo = transportVehicleNo;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }
}
