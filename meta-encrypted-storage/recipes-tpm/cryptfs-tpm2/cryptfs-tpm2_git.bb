SUMMARY = "A tool used to create, persist, evict a passphrase \
for full-disk-encryption with TPM 2.0"
DESCRIPTION = "\
This project provides with an implementation for \
creating, persisting and evicting a passphrase with TPM 2.0. \
The passphrase and its associated primary key are automatically \
created by RNG engine in TPM. In order to avoid saving the \
context file, the created passphrase and primary key are always \
persistent in TPM. \
"
AUTHOR = "Jia Zhang"
HOMEPAGE = "https://github.com/WindRiver-OpenSourceLabs/cryptfs-tpm2"
SECTION = "security/tpm"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=89c8ce1346a3dfe75379e84f3ba9d641"

DEPENDS += "tpm2-tss tpm2-abrmd pkgconfig-native"

PV = "0.7.0+git${SRCPV}"

# 0001-all-updated.patch:
# combine different patches from the original recipe into 1 patch.
# this patch is only a placeholder, does not contain the latest changes and will be overridden from the redkeep-os layer.
# the patch in the redkeep-os layer must not be placed here, but the build process must succeed even without overriding this patch.
SRC_URI = "\
    git://github.com/jiazhang0/cryptfs-tpm2.git \
    file://0001-all-updated.patch \
"
SRCREV = "87c35c63090a33d4de437f518b8da9f2d1f1d828"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "\
    sbindir="${sbindir}" \
    libdir="${libdir}" \
    includedir="${includedir}" \
    tpm2_tss_includedir="${STAGING_INCDIR}" \
    tpm2_tss_libdir="${STAGING_LIBDIR}" \
    tpm2_tabrmd_includedir="${STAGING_INCDIR}" \
    CC="${CC}" \
    CCLD="${CCLD}" \
    PKG_CONFIG="${STAGING_BINDIR_NATIVE}/pkg-config" \
    EXTRA_CFLAGS="${CFLAGS}" \
    EXTRA_LDFLAGS="${LDFLAGS}" \
"
SECURITY_LDFLAGS_remove_pn-${BPN} = "-fstack-protector-strong"

PARALLEL_MAKE = ""

do_install() {
    oe_runmake install DESTDIR="${D}"

    if [ "${@bb.utils.contains('DISTRO_FEATURES', 'luks', '1', '0', d)}" = "1" ]; then
        install -m 0500 "${S}/scripts/init.cryptfs" "${D}"
        if [ "${IS_PRODUCTION}" = "1" ]; then
            sed -i '0,/LOG_LEVEL=5/s//LOG_LEVEL=2/' ${D}/init.cryptfs
        fi
    fi
}

PACKAGES =+ "\
    ${PN}-initramfs \
"

FILES_${PN}-initramfs = "\
    /init.cryptfs \
"

# Install the minimal stuffs only, and don't care how the external
# environment is configured.

# For luks-setup.sh
# @bash: bash
# @busybox: echo, printf, cat, rm, grep
# @procps: pkill, pgrep
# @cryptsetup: cryptsetup
# @tpm2-tools: tpm2_*
# @tpm2-abrmd: optional
RDEPENDS_${PN} += "\
    libtss2 \
    libtss2-tcti-device \
    libtss2-tcti-mssim \
    ${@bb.utils.contains("INCOMPATIBLE_LICENSE", "GPL-3.0", "", "bash",d)} \
    busybox \
    procps \
    cryptsetup \
    tpm2-tools \
"

# For init.cryptfs
# @bash: bash
# @busybox: echo, printf, cat, sleep, mkdir, seq, rm, rmdir, mknod, cut, grep, awk, sed
# @kmod: depmod, modprobe
# @cryptsetup: cryptsetup
# @cryptfs-tpm2: cryptfs-tpm2
# @net-tools: ifconfig
# @util-linux: mount, umount, blkid
RDEPENDS_${PN}-initramfs += "\
    ${@bb.utils.contains("INCOMPATIBLE_LICENSE", "GPL-3.0", "", "bash",d)} \
    busybox \
    kmod \
    cryptsetup \
    cryptfs-tpm2 \
    net-tools \
    util-linux-mount \
    util-linux-umount \
    util-linux-blkid \
"

RRECOMMENDS_${PN}-initramfs += "\
    kernel-module-tpm-crb \
    kernel-module-tpm-tis \
"
