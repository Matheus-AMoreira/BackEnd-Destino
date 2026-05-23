using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BackEnd_Destino.Models;

[Table("reviews")]
public class Review
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.None)]
    [Column("id")]
    public long Id { get; set; }

    [Column("rating")]
    public int Rating { get; set; }

    [Column("comment")]
    public string? Comment { get; set; }

    [Column("review_date")]
    public DateTime ReviewDate { get; set; }

    [Column("user_id")]
    public Guid UserId { get; set; }

    [ForeignKey("UserId")]
    public virtual User? User { get; set; }

    [Column("package_id")]
    public long PackageId { get; set; }

    [ForeignKey("PackageId")]
    public virtual Package? Package { get; set; }

    [Column("created_at")]
    public DateTime? CreatedAt { get; set; }
}
