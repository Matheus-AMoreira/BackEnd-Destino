using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BackEnd_Destino.Models;

[Table("hotels")]
public class Hotel
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.None)]
    [Column("id")]
    public long Id { get; set; }

    [Column("name")]
    [MaxLength(50)]
    public string Name { get; set; } = string.Empty;

    [Column("address")]
    [MaxLength(100)]
    public string Address { get; set; } = string.Empty;

    [Column("daily_rate")]
    public decimal DailyRate { get; set; }

    [Column("city_id")]
    public long CityId { get; set; }

    [ForeignKey("CityId")]
    public virtual City? City { get; set; }
}
