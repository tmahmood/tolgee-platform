import { Box, SxProps, Theme } from '@mui/material';
import { PlanType } from './types';
import {
  IncludedCreadits,
  IncludedSeats,
  IncludedStrings,
  IncludedStringSlots,
} from '../IncludedItem';

type Props = {
  includedUsage: PlanType['includedUsage'];
  isLegacy: boolean;
  highlightColor: string;
  sx?: SxProps<Theme>;
  className?: string;
};

export const IncludedUsage = ({
  includedUsage,
  isLegacy,
  highlightColor,
  sx,
  className,
}: Props) => {
  return (
    <Box
      display="flex"
      flexDirection="column"
      justifySelf="center"
      {...{ sx, className }}
    >
      {isLegacy ? (
        <IncludedStringSlots
          className="strings"
          count={includedUsage?.translationSlots ?? -1}
          highlightColor={highlightColor}
        />
      ) : (
        <IncludedStrings
          className="strings"
          count={includedUsage?.translations ?? -1}
          highlightColor={highlightColor}
        />
      )}
      <IncludedCreadits
        className="mt-credits"
        count={includedUsage?.mtCredits ?? -1}
        highlightColor={highlightColor}
      />
      <IncludedSeats
        className="seats"
        count={includedUsage?.seats ?? -1}
        highlightColor={highlightColor}
      />
    </Box>
  );
};
