using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

using System.Text.Json.Serialization;

namespace BackEnd_Destino.Models;

[Table("packages")]
public class Package
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.None)]
    [Column("id")]
    public long Id { get; set; }

    [Column("name")]
    [MaxLength(100)]
    public string Name { get; set; } = string.Empty;

    [Column("description")]
    public string Description { get; set; } = string.Empty;

    [Column("staff_id")]
    public Guid StaffId { get; set; }

    [ForeignKey("StaffId")]
    public virtual User? Staff { get; set; }

    [Column("package_photo_id")]
    public long? PackagePhotoId { get; set; }

    [ForeignKey("PackagePhotoId")]
    [JsonPropertyName("package_photos")]
    public virtual PackagePhoto? PackagePhotos { get; set; }

    [Column("tag_ids", TypeName = "jsonb")]
    public string? TagIds { get; set; }
    
    public virtual ICollection<Offer> Offers { get; set; } = new List<Offer>();

    public virtual ICollection<Review> Reviews { get; set; } = new List<Review>();
}
