using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BackEnd_Destino.Models;

[Table("offers")]
public class Offer
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.None)]
    [Column("id")]
    public long Id { get; set; }

    [Column("price")]
    public decimal Price { get; set; }

    [Column("start_date")]
    public DateTime StartDate { get; set; }

    [Column("end_date")]
    public DateTime EndDate { get; set; }

    [Column("availability")]
    public int Availability { get; set; }

    [Column("status")]
    public OfferStatus Status { get; set; } = OfferStatus.Active;

    [Column("is_available")]
    public bool IsAvailable { get; set; } = true;

    [Column("package_id")]
    public long PackageId { get; set; }

    [ForeignKey("PackageId")]
    public virtual Package? Package { get; set; }

    [Column("hotel_id")]
    public long HotelId { get; set; }

    [ForeignKey("HotelId")]
    public virtual Hotel? Hotel { get; set; }

    [Column("transport_id")]
    public long TransportId { get; set; }

    [ForeignKey("TransportId")]
    public virtual Transport? Transport { get; set; }
}
