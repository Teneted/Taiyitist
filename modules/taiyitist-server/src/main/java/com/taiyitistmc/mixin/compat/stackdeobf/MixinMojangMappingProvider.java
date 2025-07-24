package com.taiyitistmc.mixin.compat.stackdeobf;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.taiyitistmc.util.I18n;
import dev.booky.stackdeobf.http.VerifiableUrl;
import dev.booky.stackdeobf.mappings.providers.MojangMappingProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Mixin(value = MojangMappingProvider.class, remap = false)
public class MixinMojangMappingProvider {

    @Shadow @Final private static Map<String, VerifiableUrl> STATIC_MINECRAFT_EXPERIMENTS;
    @Shadow @Final private static Gson GSON;
    @Shadow @Final private String environment;
    private static final String NEW_LICENSE =
            I18n.as("stackdeobf.mojang.licenseheader.1") + "\n"
                    + I18n.as("stackdeobf.mojang.licenseheader.2")+ "\n"
                    + I18n.as("stackdeobf.mojang.licenseheader.3")+ "\n"
                    + I18n.as("stackdeobf.mojang.licenseheader.4")+ "\n"
                    + I18n.as("stackdeobf.mojang.licenseheader.5")+ "\n"
                    + "\n";

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/StringUtils;split(Ljava/lang/String;C)[Ljava/lang/String;"))
    private String[] taiyitist$resetI18n(String str, char separatorChar) {
        return StringUtils.split(NEW_LICENSE);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;)V", ordinal = 0), index = 0)
    private String taiyitist$resetWarnInfo(String message) {
        return I18n.as("stackdeobf.mojang.license");
    }

    /**
     * @author wdog5
     * @reason i18n
     */
    @Overwrite
    private CompletableFuture<VerifiableUrl> fetchMojangMappingsUri(String mcVersion, Executor executor) {
        VerifiableUrl staticUrl = (VerifiableUrl)STATIC_MINECRAFT_EXPERIMENTS.get(mcVersion);
        if (staticUrl != null) {
            LogManager.getLogger("StackDeobfuscator").warn("Static url found for {}, using {} for downloading", mcVersion, staticUrl.getUrl());
            return staticUrl.get(executor).thenApply((resp) -> {
                JsonObject infoObj = null;

                try {
                    ByteArrayInputStream input = new ByteArrayInputStream(resp.getBody());

                    try {
                        ZipInputStream zipInput = new ZipInputStream(input);

                        ZipEntry entry;
                        try {
                            while((entry = zipInput.getNextEntry()) != null) {
                                if (entry.getName().endsWith(".json")) {
                                    Reader reader = new InputStreamReader(zipInput);

                                    try {
                                        infoObj = (JsonObject)GSON.fromJson(reader, JsonObject.class);
                                    } catch (Throwable var13) {
                                        try {
                                            ((Reader)reader).close();
                                        } catch (Throwable var12) {
                                            var13.addSuppressed(var12);
                                        }

                                        throw var13;
                                    }

                                    ((Reader)reader).close();
                                    break;
                                }
                            }
                        } catch (Throwable var14) {
                            try {
                                zipInput.close();
                            } catch (Throwable var11) {
                                var14.addSuppressed(var11);
                            }

                            throw var14;
                        }

                        zipInput.close();
                    } catch (Throwable var15) {
                        try {
                            input.close();
                        } catch (Throwable var10) {
                            var15.addSuppressed(var10);
                        }

                        throw var15;
                    }

                    input.close();
                } catch (IOException var16) {
                    IOException exception = var16;
                    throw new RuntimeException(exception);
                }

                if (infoObj == null) {
                    throw new IllegalStateException("No version metadata found in static profile for " + mcVersion);
                } else {
                    JsonObject mappingsData = infoObj.getAsJsonObject("downloads").getAsJsonObject(this.environment + "_mappings");
                    URI mappingsUrl = URI.create(mappingsData.get("url").getAsString());
                    String mappingsSha1 = mappingsData.get("sha1").getAsString();
                    return new VerifiableUrl(mappingsUrl, VerifiableUrl.HashType.SHA1, mappingsSha1);
                }
            });
        } else {
            URI manifestUri = URI.create(System.getProperty("stackdeobf.manifest-uri", "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"));
            return VerifiableUrl.resolveByMd5Header(manifestUri, executor).thenCompose((verifiableUrl) -> {
                return verifiableUrl.get(executor);
            }).thenCompose((manifestResp) -> {
                JsonObject manifestObj;
                try {
                    ByteArrayInputStream input = new ByteArrayInputStream(manifestResp.getBody());

                    try {
                        Reader reader = new InputStreamReader(input);

                        try {
                            manifestObj = (JsonObject)GSON.fromJson(reader, JsonObject.class);
                        } catch (Throwable var13) {
                            try {
                                ((Reader)reader).close();
                            } catch (Throwable var12) {
                                var13.addSuppressed(var12);
                            }

                            throw var13;
                        }

                        ((Reader)reader).close();
                    } catch (Throwable var14) {
                        try {
                            input.close();
                        } catch (Throwable var11) {
                            var14.addSuppressed(var11);
                        }

                        throw var14;
                    }

                    input.close();
                } catch (IOException var15) {
                    IOException exception = var15;
                    throw new RuntimeException(exception);
                }

                Iterator var17 = manifestObj.getAsJsonArray("versions").iterator();

                JsonObject elementObj;
                do {
                    if (!var17.hasNext()) {
                        throw new IllegalStateException(I18n.as("stackdeobf.invalid.mcversion") + " " + mcVersion + " " + I18n.as("stackdeobf.mcversion.notfound"));
                    }

                    JsonElement element = (JsonElement)var17.next();
                    elementObj = element.getAsJsonObject();
                } while(!mcVersion.equals(elementObj.get("id").getAsString()));

                URI infoUrl = URI.create(elementObj.get("url").getAsString());
                String infoSha1 = elementObj.get("sha1").getAsString();
                VerifiableUrl verifiableInfoUrl = new VerifiableUrl(infoUrl, VerifiableUrl.HashType.SHA1, infoSha1);
                return verifiableInfoUrl.get(executor).thenApply((infoResp) -> {
                    JsonObject infoObj;
                    try {
                        ByteArrayInputStream input = new ByteArrayInputStream(infoResp.getBody());

                        try {
                            Reader reader = new InputStreamReader(input);

                            try {
                                infoObj = (JsonObject)GSON.fromJson(reader, JsonObject.class);
                            } catch (Throwable var9) {
                                try {
                                    ((Reader)reader).close();
                                } catch (Throwable var8) {
                                    var9.addSuppressed(var8);
                                }

                                throw var9;
                            }

                            ((Reader)reader).close();
                        } catch (Throwable var10) {
                            try {
                                input.close();
                            } catch (Throwable var7) {
                                var10.addSuppressed(var7);
                            }

                            throw var10;
                        }

                        input.close();
                    } catch (IOException var11) {
                        IOException exception = var11;
                        throw new RuntimeException(exception);
                    }

                    JsonObject mappingsData = infoObj.getAsJsonObject("downloads").getAsJsonObject(this.environment + "_mappings");
                    URI mappingsUrl = URI.create(mappingsData.get("url").getAsString());
                    String mappingsSha1 = mappingsData.get("sha1").getAsString();
                    return new VerifiableUrl(mappingsUrl, VerifiableUrl.HashType.SHA1, mappingsSha1);
                });
            });
        }
    }
}
