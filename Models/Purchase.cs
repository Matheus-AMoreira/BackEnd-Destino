using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BackEnd_Destino.Models;

[Table("purchases")]
public class Purchase
{
    [Key]
    [Column("id")]
    public Guid Id { get; set; } = Guid.NewGuid();

    [Column("purchase_date")]
    public DateTime PurchaseDate { get; set; }

    [Column("status")]
    public PurchaseStatus Status { get; set; } = PurchaseStatus.Pending;

    [Column("method")]
    public string Method { get; set; } = string.Empty;

    [Column("payment_processor")]
    public string PaymentProcessor { get; set; } = string.Empty;

    [Column("installments")]
    public int Installments { get; set; }

    [Column("final_value")]
    public decimal FinalValue { get; set; }

    [Column("user_id")]
    public Guid UserId { get; set; }

    [ForeignKey("UserId")]
    public virtual User? User { get; set; }

    [Column("offer_id")]
    public long OfferId { get; set; }

    [ForeignKey("OfferId")]
    public virtual Offer? Offer { get; set; }

    [Column("created_at")]
    public DateTime? CreatedAt { get; set; }
}
