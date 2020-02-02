import React, { useRef, useEffect } from "react";
import { Context, Node } from "./Renderer";

interface SceneProps {
    buildRoot: (context: Context) => Node;
}

export const Scene = ({ buildRoot }: SceneProps) => {
    const ref = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const parent = ref.current!;

        const context = new Context({
            parent: parent,
        });

        const root = buildRoot(context);

        context.setRoot(root);
    }, []);

    return <div className={"Editor"} ref={ref} />;
}
