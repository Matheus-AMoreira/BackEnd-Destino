namespace BackEnd_Destino.Data;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options)
    {
    }

    public DbSet<User> Users { get; set; }
    public DbSet<Package> Packages { get; set; }
    public DbSet<PackagePhoto> PackagePhotos { get; set; }
    public DbSet<PackagePhotoItem> PackagePhotoItems { get; set; }
    public DbSet<Offer> Offers { get; set; }
    public DbSet<Hotel> Hotels { get; set; }
    public DbSet<Transport> Transports { get; set; }
    public DbSet<City> Cities { get; set; }
    public DbSet<State> States { get; set; }
    public DbSet<Region> Regions { get; set; }
    public DbSet<Purchase> Purchases { get; set; }
    public DbSet<Tag> Tags { get; set; }
    public DbSet<RefreshToken> RefreshTokens { get; set; }
    public DbSet<Role> Roles { get; set; }
    public DbSet<Permission> Permissions { get; set; }
    public DbSet<RolePermission> RolePermissions { get; set; }
    public DbSet<UserPermission> UserPermissions { get; set; }
    public DbSet<PasswordResetToken> PasswordResetTokens { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<User>(entity => {
            entity.HasIndex(e => e.Cpf).IsUnique();
            entity.HasIndex(e => e.Email).IsUnique();

            entity.Property(e => e.Cpf)
                .HasConversion(
                    v => BackEnd_Destino.Helpers.EncryptionHelper.Encrypt(v),
                    v => BackEnd_Destino.Helpers.EncryptionHelper.Decrypt(v)
                );
        });

        modelBuilder.Entity<Package>()
            .Property(p => p.TagIds)
            .HasColumnType("jsonb");
            
        modelBuilder.Entity<Offer>()
            .Property(o => o.Price)
            .HasPrecision(10, 2);
            
        modelBuilder.Entity<Purchase>()
            .Property(c => c.FinalValue)
            .HasPrecision(10, 2);

        modelBuilder.Entity<Hotel>()
            .Property(h => h.DailyRate)
            .HasPrecision(10, 2);

        modelBuilder.Entity<Transport>()
            .Property(t => t.Price)
            .HasPrecision(10, 2);

        modelBuilder.Entity<RolePermission>()
            .HasOne(rp => rp.Role)
            .WithMany(r => r.RolePermissions)
            .HasForeignKey(rp => rp.RoleId);

        modelBuilder.Entity<RolePermission>()
            .HasOne(rp => rp.Permission)
            .WithMany(p => p.RolePermissions)
            .HasForeignKey(rp => rp.PermissionId);

        modelBuilder.Entity<UserPermission>()
            .HasOne(up => up.User)
            .WithMany(u => u.UserPermissions)
            .HasForeignKey(up => up.UserId);

        modelBuilder.Entity<UserPermission>()
            .HasOne(up => up.Permission)
            .WithMany(p => p.UserPermissions)
            .HasForeignKey(up => up.PermissionId);
    }
}
