using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BackEnd_Destino.Models;

[Table("package_photo_items")]
public class PackagePhotoItem
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.None)]
    [Column("id")]
    public long Id { get; set; }

    [Column("package_photo_id")]
    public long PackagePhotoId { get; set; }

    [ForeignKey("PackagePhotoId")]
    public virtual PackagePhoto? PackagePhotos { get; set; }

    [Column("path")]
    public string Path { get; set; } = string.Empty;

    [Column("is_url")]
    public bool IsUrl { get; set; } = false;

    [Column("order")]
    public int Order { get; set; } = 0;
}
