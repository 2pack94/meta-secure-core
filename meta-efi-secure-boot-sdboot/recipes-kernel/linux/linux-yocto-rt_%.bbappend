require ${@bb.utils.contains('IMAGE_FEATURES', 'secureboot', 'linux-uefi-secure-boot.inc', '', d)}
