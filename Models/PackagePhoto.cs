using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

using System.Text.Json.Serialization;

namespace BackEnd_Destino.Models;

[Table("package_photos")]
public class PackagePhoto
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.None)]
    [Column("id")]
    public long Id { get; set; }

    [Column("name")]
    public string Name { get; set; } = string.Empty;

    [Column("storage_type")]
    public string StorageType { get; set; } = "local";

    [Column("cover_photo")]
    public string CoverPhoto { get; set; } = string.Empty;

    [Column("is_url")]
    public bool IsUrl { get; set; } = false;

    [JsonPropertyName("photos")]
    public virtual ICollection<PackagePhotoItem> Items { get; set; } = new List<PackagePhotoItem>();
}
