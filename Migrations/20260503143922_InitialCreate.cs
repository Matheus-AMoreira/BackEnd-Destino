using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace BackEnd_Destino.Migrations
{
    /// <inheritdoc />
    public partial class InitialCreate : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "package_photos",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false),
                    name = table.Column<string>(type: "text", nullable: false),
                    storage_type = table.Column<string>(type: "text", nullable: false),
                    cover_photo = table.Column<string>(type: "text", nullable: false),
                    is_url = table.Column<bool>(type: "boolean", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_package_photos", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "password_reset_tokens",
                columns: table => new
                {
                    email = table.Column<string>(type: "text", nullable: false),
                    token = table.Column<string>(type: "text", nullable: false),
                    created_at = table.Column<DateTime>(type: "timestamp with time zone", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_password_reset_tokens", x => x.email);
                });

            migrationBuilder.CreateTable(
                name: "permissions",
                columns: table => new
                {
                    id = table.Column<Guid>(type: "uuid", nullable: false),
                    slug = table.Column<string>(type: "text", nullable: false),
                    description = table.Column<string>(type: "text", nullable: true),
                    is_staff = table.Column<bool>(type: "boolean", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_permissions", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "regions",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false),
                    sigla = table.Column<string>(type: "character varying(2)", maxLength: 2, nullable: false),
                    name = table.Column<string>(type: "character varying(12)", maxLength: 12, nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_regions", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "roles",
                columns: table => new
                {
                    id = table.Column<Guid>(type: "uuid", nullable: false),
                    name = table.Column<string>(type: "text", nullable: false),
                    description = table.Column<string>(type: "text", nullable: true),
                    is_staff = table.Column<bool>(type: "boolean", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_roles", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "tags",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false),
                    name = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_tags", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "transports",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false),
                    company = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    type = table.Column<string>(type: "text", nullable: false),
                    price = table.Column<decimal>(type: "numeric(10,2)", precision: 10, scale: 2, nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_transports", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "package_photo_items",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false),
                    package_photo_id = table.Column<long>(type: "bigint", nullable: false),
                    path = table.Column<string>(type: "text", nullable: false),
                    is_url = table.Column<bool>(type: "boolean", nullable: false),
                    order = table.Column<int>(type: "integer", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_package_photo_items", x => x.id);
                    table.ForeignKey(
                        name: "FK_package_photo_items_package_photos_package_photo_id",
                        column: x => x.package_photo_id,
                        principalTable: "package_photos",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "states",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false),
                    sigla = table.Column<string>(type: "character varying(2)", maxLength: 2, nullable: false),
                    name = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    region_id = table.Column<long>(type: "bigint", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_states", x => x.id);
                    table.ForeignKey(
                        name: "FK_states_regions_region_id",
                        column: x => x.region_id,
                        principalTable: "regions",
                        principalColumn: "id");
                });

            migrationBuilder.CreateTable(
                name: "role_permissions",
                columns: table => new
                {
                    id = table.Column<Guid>(type: "uuid", nullable: false),
                    role_id = table.Column<Guid>(type: "uuid", nullable: false),
                    permission_id = table.Column<Guid>(type: "uuid", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_role_permissions", x => x.id);
                    table.ForeignKey(
                        name: "FK_role_permissions_permissions_permission_id",
                        column: x => x.permission_id,
                        principalTable: "permissions",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_role_permissions_roles_role_id",
                        column: x => x.role_id,
                        principalTable: "roles",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "users",
                columns: table => new
                {
                    id = table.Column<Guid>(type: "uuid", nullable: false),
                    nome = table.Column<string>(type: "character varying(20)", maxLength: 20, nullable: false),
                    sobre_nome = table.Column<string>(type: "character varying(20)", maxLength: 20, nullable: false),
                    slug = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    cpf = table.Column<string>(type: "character varying(255)", maxLength: 255, nullable: false),
                    email = table.Column<string>(type: "character varying(50)", maxLength: 50, nullable: false),
                    telefone = table.Column<string>(type: "character varying(11)", maxLength: 11, nullable: false),
                    password = table.Column<string>(type: "text", nullable: false),
                    is_valid = table.Column<bool>(type: "boolean", nullable: false),
                    role_id = table.Column<Guid>(type: "uuid", nullable: true),
                    email_verified_at = table.Column<DateTime>(type: "timestamp with time zone", nullable: true),
                    created_at = table.Column<DateTime>(type: "timestamp with time zone", nullable: true),
                    updated_at = table.Column<DateTime>(type: "timestamp with time zone", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_users", x => x.id);
                    table.ForeignKey(
                        name: "FK_users_roles_role_id",
                        column: x => x.role_id,
                        principalTable: "roles",
                        principalColumn: "id");
                });

            migrationBuilder.CreateTable(
                name: "cities",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false),
                    name = table.Column<string>(type: "character varying(40)", maxLength: 40, nullable: false),
                    state_id = table.Column<long>(type: "bigint", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_cities", x => x.id);
                    table.ForeignKey(
                        name: "FK_cities_states_state_id",
                        column: x => x.state_id,
                        principalTable: "states",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "packages",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false),
                    name = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    description = table.Column<string>(type: "text", nullable: false),
                    staff_id = table.Column<Guid>(type: "uuid", nullable: false),
                    package_photo_id = table.Column<long>(type: "bigint", nullable: true),
                    tag_ids = table.Column<string>(type: "jsonb", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_packages", x => x.id);
                    table.ForeignKey(
                        name: "FK_packages_package_photos_package_photo_id",
                        column: x => x.package_photo_id,
                        principalTable: "package_photos",
                        principalColumn: "id");
                    table.ForeignKey(
                        name: "FK_packages_users_staff_id",
                        column: x => x.staff_id,
                        principalTable: "users",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "refresh_tokens",
                columns: table => new
                {
                    id = table.Column<Guid>(type: "uuid", nullable: false),
                    token = table.Column<string>(type: "text", nullable: false),
                    user_id = table.Column<Guid>(type: "uuid", nullable: false),
                    expires_at = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    created_at = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    revoked_at = table.Column<DateTime>(type: "timestamp with time zone", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_refresh_tokens", x => x.id);
                    table.ForeignKey(
                        name: "FK_refresh_tokens_users_user_id",
                        column: x => x.user_id,
                        principalTable: "users",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "user_permissions",
                columns: table => new
                {
                    id = table.Column<Guid>(type: "uuid", nullable: false),
                    user_id = table.Column<Guid>(type: "uuid", nullable: false),
                    permission_id = table.Column<Guid>(type: "uuid", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_user_permissions", x => x.id);
                    table.ForeignKey(
                        name: "FK_user_permissions_permissions_permission_id",
                        column: x => x.permission_id,
                        principalTable: "permissions",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_user_permissions_users_user_id",
                        column: x => x.user_id,
                        principalTable: "users",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "hotels",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false),
                    name = table.Column<string>(type: "character varying(50)", maxLength: 50, nullable: false),
                    address = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    daily_rate = table.Column<decimal>(type: "numeric(10,2)", precision: 10, scale: 2, nullable: false),
                    city_id = table.Column<long>(type: "bigint", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_hotels", x => x.id);
                    table.ForeignKey(
                        name: "FK_hotels_cities_city_id",
                        column: x => x.city_id,
                        principalTable: "cities",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "reviews",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false),
                    rating = table.Column<int>(type: "integer", nullable: false),
                    comment = table.Column<string>(type: "text", nullable: true),
                    review_date = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    user_id = table.Column<Guid>(type: "uuid", nullable: false),
                    package_id = table.Column<long>(type: "bigint", nullable: false),
                    created_at = table.Column<DateTime>(type: "timestamp with time zone", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_reviews", x => x.id);
                    table.ForeignKey(
                        name: "FK_reviews_packages_package_id",
                        column: x => x.package_id,
                        principalTable: "packages",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_reviews_users_user_id",
                        column: x => x.user_id,
                        principalTable: "users",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "offers",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false),
                    price = table.Column<decimal>(type: "numeric(10,2)", precision: 10, scale: 2, nullable: false),
                    start_date = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    end_date = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    availability = table.Column<int>(type: "integer", nullable: false),
                    status = table.Column<int>(type: "integer", nullable: false),
                    is_available = table.Column<bool>(type: "boolean", nullable: false),
                    package_id = table.Column<long>(type: "bigint", nullable: false),
                    hotel_id = table.Column<long>(type: "bigint", nullable: false),
                    transport_id = table.Column<long>(type: "bigint", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_offers", x => x.id);
                    table.ForeignKey(
                        name: "FK_offers_hotels_hotel_id",
                        column: x => x.hotel_id,
                        principalTable: "hotels",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_offers_packages_package_id",
                        column: x => x.package_id,
                        principalTable: "packages",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_offers_transports_transport_id",
                        column: x => x.transport_id,
                        principalTable: "transports",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "purchases",
                columns: table => new
                {
                    id = table.Column<Guid>(type: "uuid", nullable: false),
                    purchase_date = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    status = table.Column<int>(type: "integer", nullable: false),
                    method = table.Column<string>(type: "text", nullable: false),
                    payment_processor = table.Column<string>(type: "text", nullable: false),
                    installments = table.Column<int>(type: "integer", nullable: false),
                    final_value = table.Column<decimal>(type: "numeric(10,2)", precision: 10, scale: 2, nullable: false),
                    user_id = table.Column<Guid>(type: "uuid", nullable: false),
                    offer_id = table.Column<long>(type: "bigint", nullable: false),
                    created_at = table.Column<DateTime>(type: "timestamp with time zone", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_purchases", x => x.id);
                    table.ForeignKey(
                        name: "FK_purchases_offers_offer_id",
                        column: x => x.offer_id,
                        principalTable: "offers",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_purchases_users_user_id",
                        column: x => x.user_id,
                        principalTable: "users",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_cities_state_id",
                table: "cities",
                column: "state_id");

            migrationBuilder.CreateIndex(
                name: "IX_hotels_city_id",
                table: "hotels",
                column: "city_id");

            migrationBuilder.CreateIndex(
                name: "IX_offers_hotel_id",
                table: "offers",
                column: "hotel_id");

            migrationBuilder.CreateIndex(
                name: "IX_offers_package_id",
                table: "offers",
                column: "package_id");

            migrationBuilder.CreateIndex(
                name: "IX_offers_transport_id",
                table: "offers",
                column: "transport_id");

            migrationBuilder.CreateIndex(
                name: "IX_package_photo_items_package_photo_id",
                table: "package_photo_items",
                column: "package_photo_id");

            migrationBuilder.CreateIndex(
                name: "IX_packages_package_photo_id",
                table: "packages",
                column: "package_photo_id");

            migrationBuilder.CreateIndex(
                name: "IX_packages_staff_id",
                table: "packages",
                column: "staff_id");

            migrationBuilder.CreateIndex(
                name: "IX_purchases_offer_id",
                table: "purchases",
                column: "offer_id");

            migrationBuilder.CreateIndex(
                name: "IX_purchases_user_id",
                table: "purchases",
                column: "user_id");

            migrationBuilder.CreateIndex(
                name: "IX_refresh_tokens_user_id",
                table: "refresh_tokens",
                column: "user_id");

            migrationBuilder.CreateIndex(
                name: "IX_reviews_package_id",
                table: "reviews",
                column: "package_id");

            migrationBuilder.CreateIndex(
                name: "IX_reviews_user_id",
                table: "reviews",
                column: "user_id");

            migrationBuilder.CreateIndex(
                name: "IX_role_permissions_permission_id",
                table: "role_permissions",
                column: "permission_id");

            migrationBuilder.CreateIndex(
                name: "IX_role_permissions_role_id",
                table: "role_permissions",
                column: "role_id");

            migrationBuilder.CreateIndex(
                name: "IX_states_region_id",
                table: "states",
                column: "region_id");

            migrationBuilder.CreateIndex(
                name: "IX_user_permissions_permission_id",
                table: "user_permissions",
                column: "permission_id");

            migrationBuilder.CreateIndex(
                name: "IX_user_permissions_user_id",
                table: "user_permissions",
                column: "user_id");

            migrationBuilder.CreateIndex(
                name: "IX_users_cpf",
                table: "users",
                column: "cpf",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_users_email",
                table: "users",
                column: "email",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_users_role_id",
                table: "users",
                column: "role_id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "package_photo_items");

            migrationBuilder.DropTable(
                name: "password_reset_tokens");

            migrationBuilder.DropTable(
                name: "purchases");

            migrationBuilder.DropTable(
                name: "refresh_tokens");

            migrationBuilder.DropTable(
                name: "reviews");

            migrationBuilder.DropTable(
                name: "role_permissions");

            migrationBuilder.DropTable(
                name: "tags");

            migrationBuilder.DropTable(
                name: "user_permissions");

            migrationBuilder.DropTable(
                name: "offers");

            migrationBuilder.DropTable(
                name: "permissions");

            migrationBuilder.DropTable(
                name: "hotels");

            migrationBuilder.DropTable(
                name: "packages");

            migrationBuilder.DropTable(
                name: "transports");

            migrationBuilder.DropTable(
                name: "cities");

            migrationBuilder.DropTable(
                name: "package_photos");

            migrationBuilder.DropTable(
                name: "users");

            migrationBuilder.DropTable(
                name: "states");

            migrationBuilder.DropTable(
                name: "roles");

            migrationBuilder.DropTable(
                name: "regions");
        }
    }
}
